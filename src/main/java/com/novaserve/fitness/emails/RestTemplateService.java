/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.emails;

import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestTemplateService {
    private final RestTemplate restTemplate;

    public RestTemplateService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Async
    public <T> T post(String url, EmailRequestBody requestBody, Class<T> responseType, String apiKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<EmailRequestBody> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, entity, responseType);

        return (T) response;
    }

    @Async
    public void postWithoutResponseBody(String url, EmailRequestBody requestBody, String apiKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<EmailRequestBody> entity = new HttpEntity<>(requestBody, headers);
        restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }
}
