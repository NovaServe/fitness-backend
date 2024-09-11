/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.helpers.builders.dto;

import com.novaserve.fitness.helpers.Util;
import com.novaserve.fitness.users.dto.request.CreateUserRequestDto;
import com.novaserve.fitness.users.model.enums.AgeGroup;
import com.novaserve.fitness.users.model.enums.Gender;
import com.novaserve.fitness.users.model.enums.Role;

public class CreateUserRequestDtoBuilder {
    private int seed;
    private Role role;
    private Gender gender;
    private AgeGroup ageGroup;
    boolean isEmpty;

    public CreateUserRequestDtoBuilder empty() {
        this.isEmpty = true;
        return this;
    }

    public CreateUserRequestDtoBuilder seed(int seed) {
        this.seed = seed;
        return this;
    }

    public CreateUserRequestDtoBuilder role(Role role) {
        this.role = role;
        return this;
    }

    public CreateUserRequestDtoBuilder gender(Gender gender) {
        this.gender = this.gender;
        return this;
    }

    public CreateUserRequestDtoBuilder ageGroup(AgeGroup ageGroup) {
        this.ageGroup = ageGroup;
        return this;
    }

    public CreateUserRequestDto build() {
        if (isEmpty) {
            return CreateUserRequestDto.builder().build();
        }
        return CreateUserRequestDto.builder()
                .username("username" + seed)
                .fullName("User Full Name " + Util.getNumberName(seed))
                .email("username" + seed + "@email.com")
                .phone("+312300000" + seed)
                .password("Password" + seed + "!")
                .confirmPassword("Password" + seed + "!")
                .role(role == null ? Role.ROLE_ADMIN : role)
                .gender(gender == null ? Gender.Male : gender)
                .ageGroup(ageGroup == null ? AgeGroup.Adult : ageGroup)
                .build();
    }
}
