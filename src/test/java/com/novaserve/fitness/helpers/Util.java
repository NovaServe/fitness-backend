/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.helpers;

import java.util.Map;

public class Util {
    private static final Map<Integer, String> MAP = Map.ofEntries(
            Map.entry(0, "Zero"),
            Map.entry(1, "One"),
            Map.entry(2, "Two"),
            Map.entry(3, "Three"),
            Map.entry(4, "Four"),
            Map.entry(5, "Five"),
            Map.entry(6, "Six"),
            Map.entry(7, "Seven"),
            Map.entry(8, "Eight"),
            Map.entry(9, "Nine"),
            Map.entry(10, "Ten"));

    public static String getNumberName(int number) {
        return MAP.get(number);
    }
}
