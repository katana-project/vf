package run.slicer.vf.teavm;

import org.jetbrains.java.decompiler.struct.StructClass;
import org.jetbrains.java.decompiler.util.DataInputFullStream;
import org.vineflower.kt.metadata.ProtoBuf;
import org.vineflower.kt.metadata.ProtoBuf$Contract$Builder;
import org.vineflower.kt.metadata.ProtoBuf$Expression$Builder;
import org.vineflower.kt.metadata.ProtoBuf$VersionRequirementTable$Builder;
import org.vineflower.kt.metadata.jvm.JvmProtoBuf;
import org.vineflower.kt.metadata.jvm.JvmProtoBuf$JvmFieldSignature$Builder;
import run.slicer.vf.teavm.classlib.java.lang.TThreadLocal;
import run.slicer.vf.teavm.classlib.java.util.concurrent.TConcurrentHashMap$KeySetView;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
import org.jetbrains.java.decompiler.main.extern.IIdentifierRenamer;
import org.jetbrains.java.decompiler.modules.renamer.ConverterHelper;

import java.io.IOException;
import java.io.UncheckedIOException;
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

    public static StructClass org_jetbrains_java_decompiler_struct_StructContext_getSentinel() {
        class Holder {
            /**
             * public class VFSentinel {
             * }
             */
            static final byte[] SENTINEL = {
                    (byte) 0xca, (byte) 0xfe, (byte) 0xba, (byte) 0xbe,
                    0x00, 0x00, 0x00, 0x41, 0x00, 0x0a, 0x0a, 0x00, 0x02, 0x00, 0x03, 0x07, 0x00, 0x04, 0x0c, 0x00,
                    0x05, 0x00, 0x06, 0x01, 0x00, 0x10, 0x6a, 0x61, 0x76, 0x61, 0x2f, 0x6c, 0x61, 0x6e, 0x67, 0x2f,
                    0x4f, 0x62, 0x6a, 0x65, 0x63, 0x74, 0x01, 0x00, 0x06, 0x3c, 0x69, 0x6e, 0x69, 0x74, 0x3e, 0x01,
                    0x00, 0x03, 0x28, 0x29, 0x56, 0x07, 0x00, 0x08, 0x01, 0x00, 0x0a, 0x56, 0x46, 0x53, 0x65, 0x6e,
                    0x74, 0x69, 0x6e, 0x65, 0x6c, 0x01, 0x00, 0x04, 0x43, 0x6f, 0x64, 0x65, 0x00, 0x21, 0x00, 0x07,
                    0x00, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x05, 0x00, 0x06, 0x00, 0x01,
                    0x00, 0x09, 0x00, 0x00, 0x00, 0x11, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x00, 0x05, 0x2a, (byte) 0xb7,
                    0x00, 0x01, (byte) 0xb1, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
            };

            static final StructClass SENTINEL_CLASS;
            static {
                try {
                    SENTINEL_CLASS = StructClass.create(new DataInputFullStream(SENTINEL), false);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }

        return Holder.SENTINEL_CLASS;
    }

    public static void java_lang_Runtime_gc(Runtime ignored) {
        TThreadLocal.removeAll();
    }

    public static ProtoBuf$VersionRequirementTable$Builder org_vineflower_kt_metadata_ProtoBuf$VersionRequirementTable_toBuilder(ProtoBuf.VersionRequirementTable ignored) {
        return new ProtoBuf$VersionRequirementTable$Builder();
    }

    public static ProtoBuf$Contract$Builder org_vineflower_kt_metadata_ProtoBuf$Contract_toBuilder(ProtoBuf.Contract ignored) {
        return new ProtoBuf$Contract$Builder();
    }

    public static ProtoBuf$Contract$Builder org_vineflower_kt_metadata_ProtoBuf$Contract_newBuilder(ProtoBuf.Contract ignored) {
        return new ProtoBuf$Contract$Builder();
    }

    public static ProtoBuf$Expression$Builder org_vineflower_kt_metadata_ProtoBuf$Expression_toBuilder(ProtoBuf.Expression ignored) {
        return new ProtoBuf$Expression$Builder();
    }

    public static JvmProtoBuf$JvmFieldSignature$Builder org_vineflower_kt_metadata_jvm_JvmProtoBuf$JvmFieldSignature_toBuilder(JvmProtoBuf.JvmFieldSignature ignored) {
        return new JvmProtoBuf$JvmFieldSignature$Builder();
    }

    public static JvmProtoBuf$JvmFieldSignature$Builder org_vineflower_kt_metadata_jvm_JvmProtoBuf$JvmFieldSignature_newBuilder(JvmProtoBuf.JvmFieldSignature ignored) {
        return new JvmProtoBuf$JvmFieldSignature$Builder();
    }
}
