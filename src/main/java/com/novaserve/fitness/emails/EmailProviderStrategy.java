/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.emails;

public interface EmailProviderStrategy {
    void send(String fullName, String confirmationUrl, String toEmail);
}
