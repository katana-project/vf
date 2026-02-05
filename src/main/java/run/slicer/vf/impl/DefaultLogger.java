package run.slicer.vf.impl;

import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSExceptions;
import org.teavm.jso.JSObject;

public final class DefaultLogger extends IFernflowerLogger {
    public static final IFernflowerLogger INSTANCE = new DefaultLogger();

    private DefaultLogger() {
    }

    @Override
    public void writeMessage(String message, Severity severity) {
        switch (severity) {
            case WARN -> warn(message);
            case ERROR -> error(message);
            default -> {
            }
        }
    }

    @Override
    public void writeMessage(String message, Severity severity, Throwable t) {
        error(message);
        errorExc(JSExceptions.getJSException(t));
    }

    @JSBody(params = "message", script = "if (console) console.warn(message);")
    private static native void warn(String message);

    @JSBody(params = "message", script = "if (console) console.error(message);")
    private static native void error(String message);

    @JSBody(params = "exc", script = "if (console) console.error(exc);")
    private static native void errorExc(JSObject exc);
}
