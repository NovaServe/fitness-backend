/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Server {
  private static final Logger logger = LoggerFactory.getLogger(Server.class);

  public static void main(String[] args) {
    SpringApplication.run(Server.class, args);
    logger.info("Server runs...");
  }
}
