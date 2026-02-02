package run.slicer.vf.teavm;

import org.teavm.model.*;
import org.teavm.model.instructions.ExitInstruction;
import org.teavm.model.instructions.NullConstantInstruction;

import java.io.File;
import java.lang.reflect.Method;

public final class MethodStubTransformer implements ClassHolderTransformer {
    private static final ValueType STRUCT_CONTEXT = ValueType.object("org.jetbrains.java.decompiler.struct.StructContext");
    private static final ValueType STRUCT_CLASS = ValueType.object("org.jetbrains.java.decompiler.struct.StructClass");

    @Override
    public void transformClass(ClassHolder cls, ClassHolderTransformerContext context) {
        switch (cls.getName()) {
            case "org.jetbrains.java.decompiler.struct.StructContext" -> {
                // ConcurrentHashMaps don't usually allow null values, but in TeaVM they do, so just patch this to return null
                this.stubWithNullConstant(cls.getMethod(new MethodDescriptor("getSentinel", STRUCT_CLASS)));
            }
            case "org.jetbrains.java.decompiler.main.plugins.JarPluginLoader" -> {
                this.stubVoid(cls.getMethod(new MethodDescriptor("init", void.class)));
            }
            case "org.jetbrains.java.decompiler.util.JrtFinder" -> {
                this.stubVoid(cls.getMethod(new MethodDescriptor("addRuntime", STRUCT_CONTEXT, ValueType.VOID)));
                this.stubVoid(cls.getMethod(new MethodDescriptor("addRuntime", STRUCT_CONTEXT, ValueType.parse(File.class), ValueType.VOID)));
            }
            case "org.jetbrains.java.decompiler.util.ClasspathScanner" -> {
                this.stubVoid(cls.getMethod(new MethodDescriptor("addAllClasspath", STRUCT_CONTEXT, ValueType.VOID)));
            }
            case "org.jetbrains.java.decompiler.modules.decompiler.ClasspathHelper" -> {
                this.stubWithNullConstant(cls.getMethod(new MethodDescriptor("findMethodOnClasspath", String.class, String.class, Method.class)));
            }
            // removing threading references to stop coroutine transformation from trying to handle them
            case "org.jetbrains.java.decompiler.util.TextBuffer$DebugTrace" -> {
                this.stubVoid(cls.getMethod(new MethodDescriptor("ensureStarted", ValueType.VOID)));
                this.stubVoid(cls.getMethod(new MethodDescriptor("checkLeaks", ValueType.VOID)));
            }
        }
    }

    private void stubVoid(MethodHolder method) {
        final Program program = this.newProgram(method.parameterCount());
        final BasicBlock block = program.createBasicBlock();

        block.add(new ExitInstruction());

        method.setProgram(program);
    }

    private void stubWithNullConstant(MethodHolder method) {
        final Program program = this.newProgram(method.parameterCount());

        final Variable nullConst = program.createVariable();
        final BasicBlock block = program.createBasicBlock();

        final var nullConstInsn = new NullConstantInstruction();
        nullConstInsn.setReceiver(nullConst);
        block.add(nullConstInsn);

        final var returnInsn = new ExitInstruction();
        returnInsn.setValueToReturn(nullConst);
        block.add(returnInsn);

        method.setProgram(program);
    }

    private Program newProgram(int parameterCount) {
        parameterCount++; // type var

        final Program program = new Program();
        for (int i = 0; i < parameterCount; i++) {
            program.createVariable();
        }

        return program;
    }
}
