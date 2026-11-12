package run.slicer.vf;

import org.teavm.jso.JSExport;
import org.teavm.jso.core.JSMapLike;
import org.teavm.jso.core.JSObjects;
import org.teavm.jso.core.JSString;
import org.teavm.jso.typedarrays.Uint8Array;
import run.slicer.vf.impl.*;

public class WASMMain extends CommonMain {
    @JSExport
    public static JSMapLike<JSString> decompile(String[] names, Options options) {
        return new WASMMain().decompile0(names, options == null || JSObjects.isUndefined(options) ? JSObjects.create() : options);
    }

    @Override
    protected byte[] source0(Options options, String name) {
        final Uint8Array b = options.source(name);
        return b == null || JSObjects.isUndefined(b) ? null : unwrapByteArray(b);
    }
}
