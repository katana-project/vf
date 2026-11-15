package run.slicer.vf;

import org.jetbrains.java.decompiler.main.Fernflower;
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences;
import org.jetbrains.java.decompiler.main.extern.TextTokenVisitor;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSByRef;
import org.teavm.jso.core.JSMapLike;
import org.teavm.jso.core.JSObjects;
import org.teavm.jso.core.JSString;
import org.teavm.jso.typedarrays.Uint8Array;
import run.slicer.vf.impl.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class CommonMain {
    protected JSMapLike<JSString> decompile0(String[] names, Options options) {
        final Map<String, Object> options0 = new HashMap<>(IFernflowerPreferences.DEFAULTS);
        options0.putAll(options.rawOptions());
        // report errors to us, not Vineflower
        options0.put(IFernflowerPreferences.ERROR_MESSAGE, "Please report this to the vf issue tracker at https://github.com/katana-project/vf/issues with a copy of the class file (if you have the rights to distribute it!)");

        final var outputSink = new OutputSinkImpl();
        final var logger = options.logger() != null ? new OptionLogger(Objects.requireNonNull(options.logger())) : DefaultLogger.INSTANCE;
        final var fernflower = new Fernflower(ResultSaverImpl.INSTANCE, options0, logger);

        if (options.tokenCollector() != null) {
            TextTokenVisitor.addVisitor(next -> new TextTokenCollector(next, options.tokenCollector()));
        }

        fernflower.addSource(new ClassSource(names, options.resources(), name0 -> source0(options, name0), outputSink));
        fernflower.addLibrary(new ResourceSource(options.resources(), name0 -> source0(options, name0)));

        fernflower.decompileContext();
        fernflower.clearContext();
        Runtime.getRuntime().gc(); // check MethodDelegates#java_lang_Runtime_gc, to clean up TThreadLocal

        final JSMapLike<JSString> output = JSObjects.create();
        for (final var entry : outputSink.output().entrySet()) {
            output.set(entry.getKey(), JSString.valueOf(entry.getValue()));
        }

        return output;
    }

    protected abstract byte[] source0(Options options, String name);

    @JSBody(params = {"data"}, script = "return data;")
    protected static native @JSByRef(optional = true) byte[] unwrapByteArray(Uint8Array data);
}
