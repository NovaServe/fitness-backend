package com.novaserve.fitness.security.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Service
public class OpenEndpoints {
    @Value("/error/**")
    private String errorURL;

    @Value("${api.basePath}/${api.version}/auth/login")
    private String loginURL;

    @Value("${api.basePath}/${api.version}/auth/logout")
    private String logoutURL;

    @Value("${api.basePath}/${api.version}/auth/validateToken")
    private String validateTokenURL;
}
