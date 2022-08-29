package com.matejbizjak.smartvillages.libutils;

import java.util.Objects;
import java.util.function.Function;

public class CommonUtil {

    public static <T, R> R safeNav(T object, Function<T, R> function) {
        Objects.requireNonNull(function);
        if (object == null) {
            return null;
        }
        return function.apply(object);
    }

    public static <T, R1, R2> R2 safeNav(T object, Function<T, R1> function1, Function<R1, R2> function2) {
        Objects.requireNonNull(function1);
        Objects.requireNonNull(function2);
        R1 r1 = safeNav(object, function1);
        if (r1 == null) {
            return null;
        }
        return function2.apply(r1);
    }
}
