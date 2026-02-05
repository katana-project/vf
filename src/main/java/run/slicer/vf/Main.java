package run.slicer.vf;

import org.jetbrains.java.decompiler.main.Fernflower;
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences;
import org.jetbrains.java.decompiler.main.extern.TextTokenVisitor;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSByRef;
import org.teavm.jso.JSExport;
import org.teavm.jso.core.JSMapLike;
import org.teavm.jso.core.JSObjects;
import org.teavm.jso.core.JSPromise;
import org.teavm.jso.core.JSString;
import org.teavm.jso.typedarrays.Uint8Array;
import run.slicer.vf.impl.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Main {
    @JSExport
    public static JSPromise<JSMapLike<JSString>> decompile(String[] names, Options options) {
        return decompile0(names, options == null || JSObjects.isUndefined(options) ? JSObjects.create() : options);
    }

    private static JSPromise<JSMapLike<JSString>> decompile0(String[] names, Options options) {
        return JSPromise.callAsync(() -> {
            final Map<String, Object> options0 = new HashMap<>(IFernflowerPreferences.DEFAULTS);
            options0.putAll(options.rawOptions());

            final var outputSink = new OutputSinkImpl();
            final var logger = options.logger() != null ? new OptionLogger(Objects.requireNonNull(options.logger())) : DefaultLogger.INSTANCE;
            final var fernflower = new Fernflower(ResultSaverImpl.INSTANCE, options0, logger);

            if (options.tokenCollector() != null) {
                TextTokenVisitor.addVisitor(next -> new TextTokenCollector(next, options.tokenCollector()));
            }

            final var source = ClassSource.create(names, options.resources(), name0 -> source0(options, name0), outputSink);
            fernflower.addSource(source);
            fernflower.addLibrary(source.librarySource());

            fernflower.decompileContext();
            fernflower.clearContext();
            Runtime.getRuntime().gc(); // check MethodDelegates#java_lang_Runtime_gc, to clean up TThreadLocal

            final JSMapLike<JSString> output = JSObjects.create();
            for (final var entry : outputSink.output().entrySet()) {
                output.set(entry.getKey(), JSString.valueOf(entry.getValue()));
            }

            return output;
        });
    }

    private static byte[] source0(Options options, String name) {
        final Uint8Array b = options.source(name).await();
        return b == null || JSObjects.isUndefined(b) ? null : unwrapByteArray(b);
    }

    @JSBody(params = {"data"}, script = "return data;")
    private static native @JSByRef(optional = true) byte[] unwrapByteArray(Uint8Array data);
}
