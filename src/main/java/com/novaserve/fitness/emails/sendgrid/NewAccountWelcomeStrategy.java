/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.emails.sendgrid;

import com.novaserve.fitness.emails.EmailProviderStrategy;
import org.springframework.stereotype.Service;

@Service
public class NewAccountWelcomeStrategy implements EmailProviderStrategy {
    @Override
    public void send(String fullName, String confirmationUrl, String toEmail) {}
}
