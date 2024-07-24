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
public class LoginResponseDto {
  private String role;

  private String fullName;
}
