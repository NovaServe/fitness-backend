/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.emails;

import com.novaserve.fitness.emails.brevo.NewAccountConfirmationStrategy;
import com.novaserve.fitness.emails.brevo.NotificationStrategy;
import com.novaserve.fitness.emails.sendgrid.NewAccountWelcomeStrategy;
import com.novaserve.fitness.exceptions.NoStrategyFound;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class EmailProviderFactory {
    private final NewAccountConfirmationStrategy newAccountConfirmationStrategy;

    private final NewAccountWelcomeStrategy newAccountWelcomeStrategy;

    private final NotificationStrategy notificationStrategy;

    private final Map<EmailProviderOption, EmailProviderStrategy> strategies;

    public EmailProviderFactory(
            NewAccountConfirmationStrategy newAccountConfirmationStrategy,
            NewAccountWelcomeStrategy newAccountWelcomeStrategy,
            NotificationStrategy notificationStrategy) {
        this.newAccountConfirmationStrategy = newAccountConfirmationStrategy;
        this.newAccountWelcomeStrategy = newAccountWelcomeStrategy;
        this.notificationStrategy = notificationStrategy;

        this.strategies = new HashMap<>();
        strategies.put(EmailProviderOption.NEW_ACCOUNT_CONFIRMATION, newAccountConfirmationStrategy);
        strategies.put(EmailProviderOption.NEW_ACCOUNT_WELCOME, newAccountWelcomeStrategy);
        strategies.put(EmailProviderOption.NOTIFICATION, notificationStrategy);
    }

    public EmailProviderStrategy getProviderStrategyInstance(EmailProviderOption emailProviderOption) {
        EmailProviderStrategy strategy = strategies.get(emailProviderOption);
        if (strategy == null) {
            throw new NoStrategyFound(emailProviderOption.name());
        }
        return strategy;
    }
}
