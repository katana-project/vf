package run.slicer.vf.impl;

import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
import org.teavm.jso.JSExceptions;
import org.teavm.jso.core.JSUndefined;
import run.slicer.vf.Options;

public final class OptionLogger extends IFernflowerLogger {
    private final Options.Logger logger;

    public OptionLogger(Options.Logger logger) {
        this.logger = logger;
    }

    private static String getLevel(Severity severity) {
        return switch (severity) {
            case TRACE -> "trace";
            case INFO -> "info";
            case WARN -> "warn";
            case ERROR -> "error";
        };
    }

    @Override
    public void writeMessage(String message, Severity severity) {
        logger.writeMessage(getLevel(severity), message, JSUndefined.instance());
    }

    @Override
    public void writeMessage(String message, Severity severity, Throwable t) {
        logger.writeMessage(getLevel(severity), message, JSExceptions.getJSException(t));
    }

    @Override
    public void startProcessingClass(String className) {
        logger.startProcessingClass(className);
    }

    @Override
    public void endProcessingClass() {
        logger.endProcessingClass();
    }

    @Override
    public void startReadingClass(String className) {
        logger.startReadingClass(className);
    }

    @Override
    public void endReadingClass() {
        logger.endReadingClass();
    }

    @Override
    public void startClass(String className) {
        logger.startClass(className);
    }

    @Override
    public void endClass() {
        logger.endClass();
    }

    @Override
    public void startMethod(String methodName) {
        logger.startMethod(methodName);
    }

    @Override
    public void endMethod() {
        logger.endMethod();
    }

    @Override
    public void startWriteClass(String className) {
        logger.startWriteClass(className);
    }

    @Override
    public void endWriteClass() {
        logger.endWriteClass();
    }
}
