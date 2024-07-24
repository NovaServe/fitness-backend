/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.dto;

import com.novaserve.fitness.validation.MatchFieldsValidation;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@MatchFieldsValidation.List({
  @MatchFieldsValidation(
      field = "password",
      fieldMatch = "confirmPassword",
      message = "Passwords don't match")
})
public class CreateUserReqDto {
  @NotBlank
  @Size(min = 5, max = 20)
  private String username;

  @Email
  @NotBlank
  @Size(min = 5, max = 40)
  private String email;

  @NotBlank
  @Size(min = 5, max = 40)
  private String fullName;

  @Size(min = 7, max = 14)
  private String phone;

  @NotBlank
  @Size(min = 8, max = 20)
  private String password;

  @NotBlank
  @Size(min = 8, max = 20)
  private String confirmPassword;

  @NotBlank private String role;

  @NotBlank private String ageGroup;

  @NotBlank private String gender;
}
