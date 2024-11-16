/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.emails.brevo;

import com.novaserve.fitness.emails.EmailProviderStrategy;
import com.novaserve.fitness.emails.RestTemplateService;
import com.novaserve.fitness.emails.share.To;
import org.springframework.stereotype.Service;

@Service
public class NewAccountConfirmationStrategy implements EmailProviderStrategy {
    private final SmtpConfigBrevo smtpConfigBrevo;

    private final RestTemplateService restTemplateService;

    public NewAccountConfirmationStrategy(SmtpConfigBrevo smtpConfigBrevo, RestTemplateService restTemplateService) {
        this.smtpConfigBrevo = smtpConfigBrevo;
        this.restTemplateService = restTemplateService;
    }

    @Override
    public void send(String fullName, String confirmationUrl, String toEmail) {
        To to = To.builder().email(toEmail).build();
        Params params = Params.builder()
                .fullName(fullName)
                .confirmationUrl(confirmationUrl)
                .build();
        NewAccountVerificationRequestBody requestBody = NewAccountVerificationRequestBody.builder()
                .to(to)
                .template_id(Integer.parseInt(smtpConfigBrevo.getNewAccountVerificationTemplateNo()))
                .params(params)
                .build();
        restTemplateService.postWithoutResponseBody(smtpConfigBrevo.getUrl(), requestBody, smtpConfigBrevo.getApiKey());
    }
}
