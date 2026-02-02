package run.slicer.vf.impl;

import org.jetbrains.java.decompiler.main.extern.IContextSource;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;
import org.jetbrains.java.decompiler.struct.consts.ConstantPool;
import org.jetbrains.java.decompiler.struct.consts.LinkConstant;
import org.jetbrains.java.decompiler.struct.consts.PooledConstant;
import org.jetbrains.java.decompiler.struct.consts.PrimitiveConstant;
import org.jetbrains.java.decompiler.struct.gen.MethodDescriptor;
import org.jetbrains.java.decompiler.util.DataInputFullStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.function.Function;

public final class ClassSource implements IContextSource {
    private final Map<String, ResourceData> data;
    private final IOutputSink sink;
    private final boolean wantLibrary;

    private ClassSource(Map<String, ResourceData> data, IOutputSink sink, boolean wantLibrary) {
        this.data = data;
        this.sink = sink;
        this.wantLibrary = wantLibrary;
    }

    public static ClassSource create(String[] names, Function<String, byte[]> source, IOutputSink sink) {
        return new ClassSource(resources(names, source), sink, false);
    }

    @Override
    public Entries getEntries() {
        final List<Entry> entries = new ArrayList<>();
        for (final ResourceData resource : this.data.values()) {
            if (resource.library() != this.wantLibrary) {
                continue;
            }

            entries.add(new Entry(resource.name(), Entry.BASE_VERSION));
        }

        return new Entries(entries, List.of(), List.of());
    }

    @Override
    public IOutputSink createOutputSink(IResultSaver saver) {
        return this.sink;
    }

    @Override
    public String getName() {
        return "vf";
    }

    @Override
    public InputStream getInputStream(String resource) {
        final ResourceData res = this.data.get(
                resource.substring(0, resource.length() - CLASS_SUFFIX.length())
        );

        return res != null ? new ByteArrayInputStream(res.data()) : null;
    }

    public IContextSource librarySource() {
        return new ClassSource(this.data, this.sink, true);
    }

    private record ResourceData(String name, byte[] data, boolean library) {
    }

    private static Map<String, ResourceData> resources(String[] names, Function<String, byte[]> source) {
        final Map<String, ResourceData> res = new HashMap<>();
        for (final String name : names) {
            res.put(name, new ResourceData(name, Objects.requireNonNull(source.apply(name), "Class " + name + " not found"), false));
        }

        new DependencyAnalyzer(names, res, source).analyze();
        return res;
    }

    private record DependencyAnalyzer(
            List<String> roots,
            Map<String, ResourceData> data,
            Function<String, byte[]> source
    ) {
        DependencyAnalyzer(String[] roots, Map<String, ResourceData> data, Function<String, byte[]> source) {
            this(List.of(roots), data, source);
        }

        public void analyze() {
            for (final ResourceData resource : List.copyOf(data.values())) {
                final var is = new DataInputFullStream(resource.data());
                try {
                    is.discard(8); // skip magic and version

                    final var cp = new ConstantPool(is);
                    for (final PooledConstant c : cp.getPool()) {
                        if (c == null) {
                            continue;
                        }

                        if (c.type == PooledConstant.CONSTANT_Class || c.type == PooledConstant.CONSTANT_MethodType) {
                            addName(((PrimitiveConstant) c).getString());
                        } else if (c.type == PooledConstant.CONSTANT_NameAndType) {
                            addName(((LinkConstant) c).descriptor);
                        }
                    }

                    // TODO: annotations, nest mates?
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }

        private void addMethodType(String descriptor) {
            try {
                final var desc = MethodDescriptor.parseDescriptor(descriptor);
                for (final var param : desc.params) {
                    if (param.value != null) {
                        addName(param.value);
                    }
                }
                if (desc.ret.value != null) {
                    addName(desc.ret.value);
                }
            } catch (Exception ignored) {
            }
        }

        private void addType(String type) {
            if (type.startsWith("[")) {
                type = type.substring(type.lastIndexOf('[') + 1);
            }
            if (type.charAt(0) == 'L' && type.charAt(type.length() - 1) == ';') {
                addName(type.substring(1, type.length() - 1));
            }
        }

        private void addName(String className) {
            if (className.isEmpty()) {
                return;
            }

            if (className.indexOf(0) == '[' || className.charAt(className.length() - 1) == ';') {
                addType(className);
            } else if (className.indexOf(0) == '(') {
                addMethodType(className);
            } else {
                if (data.containsKey(className)) {
                    return;
                }

                final byte[] b = source.apply(className);
                if (b == null) {
                    return;
                }

                data.put(className, new ResourceData(className, b, !isRootDescendant(className)));
            }
        }

        private boolean isRootDescendant(String name) {
            for (final String root : roots) {
                if (name.startsWith(root + "$")) {
                    return true;
                }
            }

            return false;
        }
    }
}
