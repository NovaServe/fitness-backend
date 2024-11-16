/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.dto.request;

import com.novaserve.fitness.profiles.model.AgeGroup;
import com.novaserve.fitness.profiles.model.Gender;
import com.novaserve.fitness.profiles.model.Role;
import com.novaserve.fitness.validation.MatchFields;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MatchFields.List({@MatchFields(field = "password", fieldMatch = "confirmPassword", message = "Passwords don't match")})
public abstract class CreateUserRequestDto {
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
