package run.slicer.vf.teavm;

import org.teavm.model.*;
import org.teavm.model.instructions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class MethodDelegationTransformer implements ClassHolderTransformer {
    private final Map<String, Set<String>> DELEGATED_METHODS = Map.of(
            "java.util.concurrent.ConcurrentHashMap", Set.of("newKeySet"),
            "org.jetbrains.java.decompiler.main.Fernflower", Set.of("loadHelper"),
            "java.lang.Runtime", Set.of("gc")
    );

    @Override
    public void transformClass(ClassHolder cls, ClassHolderTransformerContext context) {
        for (final MethodHolder method : cls.getMethods()) {
            if (method.getProgram() == null) continue;

            for (final BasicBlock block : method.getProgram().getBasicBlocks()) {
                for (final Instruction insn : block) {
                    if (insn instanceof InvokeInstruction invokeInsn) {
                        this.transformInvokeInsn(invokeInsn);
                    }
                }
            }
        }
    }

    private void transformInvokeInsn(InvokeInstruction insn) {
        final MethodReference method = insn.getMethod();

        final Set<String> candidates = DELEGATED_METHODS.get(method.getClassName());
        if (candidates == null || !candidates.contains(method.getName())) return;

        final String name = (method.getClassName() + "_" + method.getName()).replace('.', '_');

        final List<ValueType> signature = new ArrayList<>(List.of(method.getSignature()));
        if (insn.getInstance() != null) { // not a static invocation, remap "this" ref as an argument
            signature.addFirst(ValueType.object(method.getClassName()));

            final List<Variable> args = new ArrayList<>(insn.getArguments());
            args.addFirst(insn.getInstance());

            insn.setInstance(null);
            insn.setArguments(args.toArray(new Variable[0]));
        }

        insn.setMethod(new MethodReference("run.slicer.vf.teavm.MethodDelegates", name, signature.toArray(new ValueType[0])));
    }
}
