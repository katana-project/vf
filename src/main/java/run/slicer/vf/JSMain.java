package run.slicer.vf;

import org.teavm.jso.JSExport;
import org.teavm.jso.core.JSMapLike;
import org.teavm.jso.core.JSObjects;
import org.teavm.jso.core.JSPromise;
import org.teavm.jso.core.JSString;
import org.teavm.jso.typedarrays.Uint8Array;

public class JSMain extends CommonMain {
    @JSExport
    public static JSPromise<JSMapLike<JSString>> decompile(String[] names, Options options) {
        return JSPromise.callAsync(() -> new JSMain().decompile0(names, options == null || JSObjects.isUndefined(options) ? JSObjects.create() : options));
    }

    @Override
    protected byte[] source0(Options options, String name) {
        final Uint8Array b = options.sourceAsync(name).await();
        return b == null || JSObjects.isUndefined(b) ? null : unwrapByteArray(b);
    }
}
