package run.slicer.vf.impl;

import org.jetbrains.java.decompiler.main.extern.IContextSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.function.Function;

public abstract class AbstractContextSource implements IContextSource {
    protected final Function<String, byte[]> source;

    protected AbstractContextSource(Function<String, byte[]> source) {
        this.source = source;
    }

    @Override
    public String getName() {
        return "vf";
    }

    @Override
    public InputStream getInputStream(String resource) {
        final byte[] b = this.source.apply(
                resource.substring(0, resource.length() - CLASS_SUFFIX.length())
        );

        return b != null ? new ByteArrayInputStream(b) : null;
    }

    public Function<String, byte[]> source() {
        return this.source;
    }
}
