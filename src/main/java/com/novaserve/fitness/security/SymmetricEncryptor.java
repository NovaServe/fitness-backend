/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.security;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SymmetricEncryptor {
    private static final String ALGORITHM = "AES";
    private static final String MODE = "CBC";
    private static final String PADDING = "PKCS5Padding";
    private static final int KEY_SIZE = 128;
    private static final int IV_SIZE = 16; // AES block size is 128 bits (= 16 bytes)

    public static String encrypt(String plaintext, String keyBase64) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM);

        Cipher cipher = Cipher.getInstance(ALGORITHM + "/" + MODE + "/" + PADDING);
        byte[] ivBytes = generateIV();
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        byte[] encrypted = cipher.doFinal(plaintext.getBytes("UTF-8"));

        byte[] combined = new byte[ivBytes.length + encrypted.length];
        System.arraycopy(ivBytes, 0, combined, 0, ivBytes.length);
        System.arraycopy(encrypted, 0, combined, ivBytes.length, encrypted.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    public static String decrypt(String ciphertextBase64, String keyBase64) throws Exception {
        byte[] combined = Base64.getDecoder().decode(ciphertextBase64);
        byte[] ivBytes = new byte[IV_SIZE];
        byte[] encrypted = new byte[combined.length - IV_SIZE];
        System.arraycopy(combined, 0, ivBytes, 0, IV_SIZE);
        System.arraycopy(combined, IV_SIZE, encrypted, 0, encrypted.length);

        byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM);

        Cipher cipher = Cipher.getInstance(ALGORITHM + "/" + MODE + "/" + PADDING);
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        byte[] decrypted = cipher.doFinal(encrypted);

        return new String(decrypted, "UTF-8");
    }

    private static byte[] generateIV() {
        byte[] iv = new byte[IV_SIZE];
        for (int i = 0; i < IV_SIZE; i++) {
            iv[i] = (byte) (Math.random() * 256);
        }
        return iv;
    }

    public static void main(String[] args) throws Exception {
        String plaintext = "Hello, World!";
        String keyBase64 = "5+9HDu5zd7sF2Ywq/Nh9uw=="; // Example 128-bit key in Base64

        String encrypted = encrypt(plaintext, keyBase64);
        System.out.println("Encrypted: " + encrypted);

        String decrypted = decrypt(encrypted, keyBase64);
        System.out.println("Decrypted: " + decrypted);
    }
}
