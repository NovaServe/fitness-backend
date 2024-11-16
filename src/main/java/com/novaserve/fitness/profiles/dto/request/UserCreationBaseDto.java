/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.novaserve.fitness.profiles.model.AgeGroup;
import com.novaserve.fitness.profiles.model.Gender;
import com.novaserve.fitness.profiles.model.Role;
import com.novaserve.fitness.validation.MatchFields;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MatchFields.List({@MatchFields(field = "password", fieldMatch = "confirmPassword", message = "Passwords don't match")})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = CustomerCreationDto.class, name = "CUSTOMER"),
    @JsonSubTypes.Type(value = InstructorCreationDto.class, name = "INSTRUCTOR"),
    @JsonSubTypes.Type(value = AdminCreationDto.class, name = "ADMIN")
})
public abstract class UserCreationBaseDto {
    UserCreationDtoType type;

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
