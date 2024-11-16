/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.emails.brevo;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SmtpConfigBrevo {
    @Value("${smtp.brevo.url}")
    private String url;

    @Value("${smtp.brevo.api-key}")
    private String apiKey;

    @Value("${smtp.brevo.new-account-verification-template-no}")
    private String newAccountVerificationTemplateNo;
}
