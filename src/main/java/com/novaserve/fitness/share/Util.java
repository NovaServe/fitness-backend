/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.share;

import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;

public class Util {
    public static <T> int findIndexByPredicate(List<T> list, Predicate<T> predicate) {
        ListIterator<T> iterator = list.listIterator();
        while (iterator.hasNext()) {
            T element = iterator.next();
            if (predicate.test(element)) {
                return iterator.previousIndex();
            }
        }
        return -1;
    }
}
