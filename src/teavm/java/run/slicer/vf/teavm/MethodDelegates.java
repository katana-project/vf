package run.slicer.vf.teavm;

import run.slicer.vf.teavm.classlib.java.lang.TThreadLocal;
import run.slicer.vf.teavm.classlib.java.util.concurrent.TConcurrentHashMap$KeySetView;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
import org.jetbrains.java.decompiler.main.extern.IIdentifierRenamer;
import org.jetbrains.java.decompiler.modules.renamer.ConverterHelper;

import java.util.concurrent.ConcurrentHashMap;

public final class MethodDelegates {
    private MethodDelegates() {
    }

    @SuppressWarnings({"unchecked", "DataFlowIssue"})
    public static <K> ConcurrentHashMap.KeySetView<K, Boolean> java_util_concurrent_ConcurrentHashMap_newKeySet() {
        return (ConcurrentHashMap.KeySetView<K, Boolean>) ((Object) (new TConcurrentHashMap$KeySetView<>(new ConcurrentHashMap<>(), Boolean.TRUE)));
    }

    public static IIdentifierRenamer org_jetbrains_java_decompiler_main_Fernflower_loadHelper(String ignored, IFernflowerLogger ignored1) {
        return new ConverterHelper();
    }

    public static void java_lang_Runtime_gc(Runtime ignored) {
        TThreadLocal.removeAll();
    }
}
