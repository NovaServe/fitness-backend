/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginReqDto {
  @NotBlank
  @Size(min = 5, max = 40)
  private String usernameOrEmailOrPhone;

  @NotBlank
  @Size(min = 8, max = 20)
  private String password;
}
