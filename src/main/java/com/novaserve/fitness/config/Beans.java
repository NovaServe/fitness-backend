/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class Beans {

  @Bean
  public ModelMapper modelMapper() {
    // Add custom configurations here, if needed. Example:
    // ModelMapper modelMapper = new ModelMapper();
    // modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    return new ModelMapper();
  }

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();
    return objectMapper;
  }

  @Profile("local")
  @Bean
  public String profileLocal() {
    String v = ">> Profile local";
    System.out.println(v);
    return v;
  }

  @Profile("prod")
  @Bean
  public String profileProd() {
    String v = ">> Profile prod";
    System.out.println(v);
    return v;
  }

  @Profile("dev")
  @Bean
  public String profileDev() {
    String v = ">> Profile dev";
    System.out.println(v);
    return v;
  }
}
