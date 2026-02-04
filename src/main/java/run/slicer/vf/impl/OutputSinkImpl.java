package run.slicer.vf.impl;

import org.jetbrains.java.decompiler.main.extern.IContextSource;

import java.util.HashMap;
import java.util.Map;

public final class OutputSinkImpl implements IContextSource.IOutputSink {
    private final Map<String, String> output = new HashMap<>();

    @Override
    public void begin() {
    }

    @Override
    public void acceptClass(String qualifiedName, String fileName, String content, int[] mapping) {
        this.output.put(qualifiedName, content);
    }

    @Override
    public void acceptDirectory(String directory) {
    }

    @Override
    public void acceptOther(String path) {
    }

    @Override
    public void close() {
    }

    public Map<String, String> output() {
        return this.output;
    }
}
