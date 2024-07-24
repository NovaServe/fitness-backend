/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class Porfiles {

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
