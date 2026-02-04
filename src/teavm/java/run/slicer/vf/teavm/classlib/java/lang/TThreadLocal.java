package run.slicer.vf.teavm.classlib.java.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TThreadLocal<T> {
    private static final List<TThreadLocal<?>> ALL = new ArrayList<>();
    private final Supplier<? extends T> supplier;
    private boolean initialized;
    private T value;

    private TThreadLocal(Supplier<? extends T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        if (!initialized && supplier != null) {
            value = supplier.get();
            initialized = true;
            ALL.add(this);
        }
        return value;
    }

    public void set(T value) {
        initialized = true;
        this.value = value;
        if (!ALL.contains(this)) {
            ALL.add(this);
        }
    }

    private void remove() {
        initialized = false;
        value = null;
        ALL.remove(this);
    }

    public static <S> TThreadLocal<S> withInitial(Supplier<? extends S> supplier) {
        return new TThreadLocal<S>(supplier);
    }

    public static void removeAll() {
        for (final TThreadLocal<?> threadLocal : List.copyOf(ALL)) {
            threadLocal.remove();
        }
    }
}
