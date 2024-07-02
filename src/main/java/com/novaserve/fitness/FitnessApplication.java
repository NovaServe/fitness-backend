package com.novaserve.fitness;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class FitnessApplication {
    private static final Logger logger = LoggerFactory.getLogger(FitnessApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(FitnessApplication.class, args);
        logger.info("Application starts!");
    }
}
