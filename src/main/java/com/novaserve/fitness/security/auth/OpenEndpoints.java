/*
** Copyright (C) 2024 NovaServe
*/
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
  private String errorUrl;

  @Value("${api.basePath}/${api.version}/auth/login")
  private String loginUrl;

  @Value("${api.basePath}/${api.version}/auth/logout")
  private String logoutUrl;

  @Value("${api.basePath}/${api.version}/auth/validateToken")
  private String validateTokenUrl;
}
