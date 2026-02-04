package run.slicer.vf.impl;

import org.jetbrains.java.decompiler.main.extern.IContextSource;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;
import org.jetbrains.java.decompiler.struct.consts.ConstantPool;
import org.jetbrains.java.decompiler.struct.consts.LinkConstant;
import org.jetbrains.java.decompiler.struct.consts.PooledConstant;
import org.jetbrains.java.decompiler.struct.consts.PrimitiveConstant;
import org.jetbrains.java.decompiler.struct.gen.CodeType;
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

    public static ClassSource create(String[] names, String[] resources, Function<String, byte[]> source, IOutputSink sink) {
        return new ClassSource(resources(names, resources, source), sink, false);
    }

    @Override
    public Entries getEntries() {
        final List<Entry> entries = new ArrayList<>();
        for (final ResourceData resource : this.data.values()) {
            if (resource == null || resource.library() != this.wantLibrary) {
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

    private static Map<String, ResourceData> resources(String[] names, String[] resources, Function<String, byte[]> source) {
        final Map<String, ResourceData> res = new HashMap<>();
        for (final String name : names) {
            res.put(name, new ResourceData(name, Objects.requireNonNull(source.apply(name), "Class " + name + " not found"), false));
        }

        for (final String resource : resources) {
            for (final String name : names) {
                if (resource.startsWith(name + '$') && !res.containsKey(resource)) {
                    // quick workaround for a Vineflower bug/quirk:
                    // inner classes need to be supplied in the base source, it does not find them in the library source
                    final byte[] b = source.apply(resource);
                    res.put(resource, b != null ? new ResourceData(resource, b, false) : null);
                }
            }
        }

        new DependencyAnalyzer(res, source).analyze();
        return res;
    }

    private record DependencyAnalyzer(Map<String, ResourceData> data, Function<String, byte[]> source) {
        void analyze() {
            for (final ResourceData resource : new ArrayList<>(data.values())) {
                if (resource == null) {
                    continue;
                }

                final var is = new DataInputFullStream(resource.data());
                try {
                    is.discard(8); // skip magic and version

                    final var cp = new ConstantPool(is);
                    for (final PooledConstant c : cp.getPool()) {
                        if (c == null) {
                            continue;
                        }

                        switch (c.type) {
                            case PooledConstant.CONSTANT_MethodType -> addMethodType(((PrimitiveConstant) c).getString());
                            case PooledConstant.CONSTANT_Class -> {
                                final String name = ((PrimitiveConstant) c).getString();
                                if (name.charAt(0) == '[') {
                                    addType(name);
                                } else {
                                    addName(name);
                                }
                            }
                            case PooledConstant.CONSTANT_NameAndType -> {
                                final String desc = ((LinkConstant) c).descriptor;
                                if (desc.charAt(0) == '(') {
                                    addMethodType(desc);
                                } else {
                                    addType(desc);
                                }
                            }
                        }
                    }

                    // TODO: annotations, nest mates?
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }

        private void addMethodType(String descriptor) {
            if (descriptor == null || descriptor.isEmpty()) {
                return;
            }

            try {
                final var desc = MethodDescriptor.parseDescriptor(descriptor);
                for (final var param : desc.params) {
                    if (param.type == CodeType.OBJECT) {
                        addName(param.value);
                    }
                }
                if (desc.ret.type == CodeType.OBJECT) {
                    addName(desc.ret.value);
                }
            } catch (Exception ignored) {
            }
        }

        private void addType(String type) {
            if (type == null || type.isEmpty()) {
                return;
            }

            if (type.charAt(0) == '[') {
                type = type.substring(type.lastIndexOf('[') + 1);
            }
            if (type.charAt(0) == 'L' && type.charAt(type.length() - 1) == ';') {
                addName(type.substring(1, type.length() - 1));
            }
        }

        private void addName(String className) {
            if (className == null || className.isEmpty() || data.containsKey(className)) {
                return;
            }

            final byte[] b = source.apply(className);
            data.put(className, b != null ? new ResourceData(className, b, true) : null);
        }
    }
}
