/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.security.auth;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Getter
@Service
public class OpenEndpoints {
    private final String errorUrl;

    private final String loginUrl;

    private final String logoutUrl;

    private final String validateTokenUrl;

    public OpenEndpoints(
            @Value("/error/**") String errorUrl,
            @Value("${api.basePath}/${api.version}/auth/login") String loginUrl,
            @Value("${api.basePath}/${api.version}/auth/logout") String logoutUrl,
            @Value("${api.basePath}/${api.version}/auth/validateToken") String validateTokenUrl) {
        this.errorUrl = errorUrl;
        this.loginUrl = loginUrl;
        this.logoutUrl = logoutUrl;
        this.validateTokenUrl = validateTokenUrl;
    }
}
