package run.slicer.vf;

import org.jetbrains.annotations.Nullable;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.core.JSPromise;
import org.teavm.jso.typedarrays.Uint8Array;

import java.util.HashMap;
import java.util.Map;

public interface Options extends JSObject {
    @JSBody(script = "return this.options ? Object.entries(this.options) : [];")
    Option[] options();

    default Map<String, String> rawOptions() {
        final Map<String, String> options = new HashMap<>();
        for (final Options.Option option : this.options()) {
            options.put(option.name(), option.value());
        }

        return options;
    }

    @JSBody(params = {"name"}, script = "return this.source ? this.source(name) : Promise.resolve(null);")
    JSPromise<Uint8Array> source(String name);

    @JSBody(script = "return this.resources || [];")
    String[] resources();

    @JSBody(script = "return this.tokenCollector || null;")
    @Nullable
    TokenCollector tokenCollector();

    interface Option extends JSObject {
        @JSBody(script = "return this[0];")
        String name();

        @JSBody(script = "return this[1];")
        String value();
    }

    /**
     * {@link org.jetbrains.java.decompiler.main.extern.TextTokenVisitor}
     */
    interface TokenCollector extends JSObject {
        @JSBody(params = {"content"}, script = "this.start(content);")
        void start(String content);

        @JSBody(params = {"start", "length", "declaration", "name"}, script = "this.visitClass(start, length, declaration, name);")
        void visitClass(int start, int length, boolean declaration, String name);

        @JSBody(params = {"start", "length", "declaration", "className", "name", "descriptor"}, script = "this.visitField(start, length, declaration, className, name, descriptor);")
        void visitField(int start, int length, boolean declaration, String className, String name, String descriptor);

        @JSBody(params = {"start", "length", "declaration", "className", "name", "descriptor"}, script = "this.visitMethod(start, length, declaration, className, name, descriptor);")
        void visitMethod(int start, int length, boolean declaration, String className, String name, String descriptor);

        @JSBody(params = {"start", "length", "declaration", "className", "methodName", "methodDescriptor", "index", "name"}, script = "this.visitParameter(start, length, declaration, className, methodName, methodDescriptor, index, name);")
        void visitParameter(int start, int length, boolean declaration, String className, String methodName, String methodDescriptor, int index, String name);

        @JSBody(params = {"start", "length", "declaration", "className", "methodName", "methodDescriptor", "index", "name"}, script = "this.visitLocal(start, length, declaration, className, methodName, methodDescriptor, index, name);")
        void visitLocal(int start, int length, boolean declaration, String className, String methodName, String methodDescriptor, int index, String name);

        @JSBody(script = "this.end();")
        void end();
    }
}
