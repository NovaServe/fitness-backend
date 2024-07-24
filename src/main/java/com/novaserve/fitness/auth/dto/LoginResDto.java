/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.auth.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResDto {
  private String role;
  private String fullName;
}
