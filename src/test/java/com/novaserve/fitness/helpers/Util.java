/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.helpers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestComponent
public class Util {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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

    public static String generateTextWithSeed(String text, int seed) {
        return text + seed;
    }

    public String generateTrainingTitleWithSeed(int seed) {
        return "Training " + seed;
    }

    public static LocalTime convertToTime(int hour, int minute) {
        return LocalTime.of(hour, minute);
    }

    public static LocalDate convertToDate(int year, int month, int day) {
        return LocalDate.of(year, month, day);
    }
}
