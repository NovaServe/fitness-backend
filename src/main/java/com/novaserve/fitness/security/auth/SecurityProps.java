/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.security.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security")
public record SecurityProps(Jwt Jwt) {
    public record Jwt(long expiresInMilliseconds, String secret) {}
}
