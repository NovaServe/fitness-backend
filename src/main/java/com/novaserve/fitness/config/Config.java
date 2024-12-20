/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @Profile("local")
    public String profileLocal() {
        logger.info("Profile local");
        return null;
    }

    @Bean
    @Profile("dev")
    public String profileDev() {
        logger.info("Profile dev");
        return null;
    }

    @Profile("prod")
    @Bean
    public String profileProd() {
        logger.info("Profile prod");
        return null;
    }
}
