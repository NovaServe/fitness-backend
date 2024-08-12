/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.dto;

import com.novaserve.fitness.users.model.AgeGroup;
import com.novaserve.fitness.users.model.Gender;
import com.novaserve.fitness.users.model.Role;
import com.novaserve.fitness.validation.MatchFields;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@MatchFields.List({@MatchFields(field = "password", fieldMatch = "confirmPassword", message = "Passwords don't match")})
public class CreateUserRequestDto {
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

    @NotNull
    private Role role;

    private AgeGroup ageGroup;

    private Gender gender;
}
