package run.slicer.vf.impl;

import org.jetbrains.java.decompiler.main.extern.IResultSaver;

import java.util.*;
import java.util.function.Function;

public final class ClassSource extends AbstractContextSource {
    private final String[] names;
    private final IOutputSink sink;

    public ClassSource(String[] names, String[] resources, Function<String, byte[]> source, IOutputSink sink) {
        super(source);
        this.names = resources(names, resources);
        this.sink = sink;
    }

    @Override
    public Entries getEntries() {
        final List<Entry> entries = new ArrayList<>();
        for (final String name : this.names) {
            entries.add(new Entry(name, Entry.BASE_VERSION));
        }

        return new Entries(entries, List.of(), List.of());
    }

    @Override
    public IOutputSink createOutputSink(IResultSaver saver) {
        return this.sink;
    }

    public String[] names() {
        return this.names;
    }

    // quick workaround for a Vineflower bug/quirk:
    // inner classes need to be supplied in the base source, it does not find them in the library source
    private static String[] resources(String[] names, String[] resources) {
        final Set<String> namesSet = new HashSet<>(Arrays.asList(names));
        for (final String resource : resources) {
            for (final String name : names) {
                if (resource.startsWith(name + '$')) {
                    namesSet.add(resource);
                }
            }
        }

        return namesSet.toArray(new String[0]);
    }
}
