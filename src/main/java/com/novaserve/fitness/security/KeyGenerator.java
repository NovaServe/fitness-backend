/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.security;

import java.security.SecureRandom;
import java.util.Base64;

public class KeyGenerator {
    public static void main(String[] args) {
        try {
            // Generate a 128-bit AES key
            byte[] keyBytes = gen128BitKey();
            // Convert the key bytes to Base64 for storage or transmission
            String keyBase64 = Base64.getEncoder().encodeToString(keyBytes);
            System.out.println("Generated 128-bit AES key (Base64): " + keyBase64);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] gen128BitKey() throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[16]; // 16 bytes = 128 bits
        secureRandom.nextBytes(key);
        return key;
    }
}
