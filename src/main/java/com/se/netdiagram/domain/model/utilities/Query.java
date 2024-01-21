package com.se.netdiagram.domain.model.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class Query {
    public static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        for (T element : list) {
            if (predicate.test(element)) {
                result.add(element);
            }
        }
        return result;
    }

    public static <T, M> List<M> map(List<T> list, Function<T, M> function) {
        List<M> result = new ArrayList<>();
        for (T element : list) {
            result.add(function.apply(element));
        }
        return result;
    }

    public static <T, M> List<M> filterAndMap(List<T> list, Predicate<T> predicate, Function<T, M> function) {
        List<M> result = new ArrayList<>();
        for (T element : list) {
            if (predicate.test(element)) {
                result.add(function.apply(element));
            }
        }
        return result;
    }

    public static <T> boolean any(List<T> list, Predicate<T> predicate) {
        for (T element : list) {
            if (predicate.test(element)) {
                return true;
            }
        }
        return false;
    }

}
