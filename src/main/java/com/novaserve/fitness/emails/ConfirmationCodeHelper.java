/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.emails;

import com.novaserve.fitness.emails.model.ConfirmationCode;
import com.novaserve.fitness.emails.repository.ConfirmationCodeRepository;
import com.novaserve.fitness.profiles.model.UserBase;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class ConfirmationCodeHelper {
    private final ConfirmationCodeRepository confirmationCodeRepository;

    public ConfirmationCodeHelper(ConfirmationCodeRepository confirmationCodeRepository) {
        this.confirmationCodeRepository = confirmationCodeRepository;
    }

    public String generateConfirmationCodeAndSave(UserBase user) throws NoSuchAlgorithmException {
        String code = generateConfirmationCode(user.getId());
        ConfirmationCode confirmationCode =
                ConfirmationCode.builder().code(code).user(user).build();
        confirmationCodeRepository.save(confirmationCode);
        return code;
    }

    private String generateConfirmationCode(long userId) throws NoSuchAlgorithmException {
        long timestamp = System.currentTimeMillis();
        String data = userId + "-" + timestamp;

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data.getBytes());

        String confirmationCode = Base64.getUrlEncoder().encodeToString(hash);
        return confirmationCode;
    }
}
