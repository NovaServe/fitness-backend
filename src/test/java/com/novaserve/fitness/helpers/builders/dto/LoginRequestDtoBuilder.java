/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.helpers.builders.dto;

import com.novaserve.fitness.auth.dto.LoginRequestDto;

public class LoginRequestDtoBuilder {
    private int seed;
    private boolean withUsername;
    private boolean withEmail;
    private boolean withPhone;
    private boolean isEmpty;

    public LoginRequestDtoBuilder empty() {
        this.isEmpty = true;
        return this;
    }

    public LoginRequestDtoBuilder seed(int seed) {
        this.seed = seed;
        return this;
    }

    public LoginRequestDtoBuilder withUsername() {
        this.withUsername = true;
        return this;
    }

    public LoginRequestDtoBuilder withEmail() {
        this.withEmail = true;
        return this;
    }

    public LoginRequestDtoBuilder withPhone() {
        this.withPhone = true;
        return this;
    }

    public LoginRequestDto build() {
        if (isEmpty) {
            return LoginRequestDto.builder().build();
        }
        String usernameOrEmailOrPhone = null;
        if (withUsername) {
            usernameOrEmailOrPhone = "username" + seed;
        } else if (withEmail) {
            usernameOrEmailOrPhone = "username" + seed + "@email.com";
        } else if (withPhone) {
            usernameOrEmailOrPhone = "+312300000" + seed;
        }
        return LoginRequestDto.builder()
                .usernameOrEmailOrPhone(usernameOrEmailOrPhone)
                .password("Password" + seed + "!")
                .build();
    }
}
