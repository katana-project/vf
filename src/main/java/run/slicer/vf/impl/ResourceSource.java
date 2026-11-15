package run.slicer.vf.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class ResourceSource extends AbstractContextSource {
    private final String[] names;

    public ResourceSource(String[] names, Function<String, byte[]> source) {
        super(source);
        this.names = names;
    }

    @Override
    public Entries getEntries() {
        final List<Entry> entries = new ArrayList<>();
        for (final String name : this.names) {
            entries.add(new Entry(name, Entry.BASE_VERSION));
        }

        return new Entries(entries, List.of(), List.of());
    }

    public String[] names() {
        return this.names;
    }
}
