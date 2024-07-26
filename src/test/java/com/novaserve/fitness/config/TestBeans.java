/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaserve.fitness.helpers.DbHelper;
import com.novaserve.fitness.helpers.DtoHelper;
import com.novaserve.fitness.helpers.MockHelper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestConfiguration
public class TestBeans {
    @Bean
    public DtoHelper dtoHelper() {
        return new DtoHelper();
    }

    @Bean
    public MockHelper mockHelper() {
        return new MockHelper();
    }

    @Bean
    public DbHelper dbHelper() {
        return new DbHelper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
