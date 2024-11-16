/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.helpers.builders.profiles;

import com.novaserve.fitness.helpers.Util;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserUtil {
    public static String generateUsernameWithSeed(int seed) {
        return "username" + seed;
    }

    public static String generateFullNameWithSeed(int seed) {
        return "User Full Name " + Util.getNumberName(seed);
    }

    public static String generateEmailWithSeed(int seed) {
        return "username" + seed + "@email.com";
    }

    public static String generatePhoneWithSeed(int seed) {
        return "+312300000" + seed;
    }

    public static String generateHashedPasswordWithSeed(int seed) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode("Password" + seed + "!");
    }

    public static String generateRawPasswordWithSeed(int seed) {
        return "Password" + seed + "!";
    }

    public static String generateAreaNameWithSeed(int seed) {
        return "Area " + seed;
    }
}
