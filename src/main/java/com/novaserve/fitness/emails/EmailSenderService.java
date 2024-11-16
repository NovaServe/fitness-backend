/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.emails;

import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {
    private final EmailProviderFactory emailProviderFactory;

    public EmailSenderService(EmailProviderFactory emailProviderFactory) {
        this.emailProviderFactory = emailProviderFactory;
    }

    public void sendEmailOnNewAccountCreationAsync(
            String receiverFullName, String receiverEmail, String confirmationUrl) {
        EmailProviderStrategy newAccountConfirmationStrategy =
                emailProviderFactory.getProviderStrategyInstance(EmailProviderOption.NEW_ACCOUNT_CONFIRMATION);
        newAccountConfirmationStrategy.send(receiverFullName, confirmationUrl, receiverEmail);

        EmailProviderStrategy newAccountWelcomeStrategy =
                emailProviderFactory.getProviderStrategyInstance(EmailProviderOption.NEW_ACCOUNT_WELCOME);
        newAccountConfirmationStrategy.send(receiverFullName, confirmationUrl, receiverEmail);
    }
}
