package net.johanbasson.rail.func;

public interface Function2<T1, T2, R> {

    R apply(T1 t1, T2 t2);

    default Function1<T1, Function1<T2, R>> curried() {
        return t1 -> t2 -> apply(t1, t2);
    }
}
