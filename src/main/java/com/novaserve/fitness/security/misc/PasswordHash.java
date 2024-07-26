/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.security.misc;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordHash {
    public static void main(String[] args) {
        System.out.println(hashPassword(""));
    }

    public static String hashPassword(String password) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }
}
