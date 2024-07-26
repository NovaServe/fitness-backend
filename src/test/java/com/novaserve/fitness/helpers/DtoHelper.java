/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.helpers;

import com.novaserve.fitness.auth.dto.LoginRequestDto;
import com.novaserve.fitness.users.dto.CreateUserRequestDto;

public class DtoHelper {
    public CreateUserRequestDtoBuilder createUserRequestDto() {
        return new CreateUserRequestDtoBuilder();
    }

    public static class CreateUserRequestDtoBuilder {
        private int seed;
        private String roleName;
        private String genderName;
        private String ageGroupName;
        boolean isEmpty;

        public CreateUserRequestDtoBuilder empty() {
            this.isEmpty = true;
            return this;
        }

        public CreateUserRequestDtoBuilder seed(int seed) {
            this.seed = seed;
            return this;
        }

        public CreateUserRequestDtoBuilder role(String roleName) {
            this.roleName = roleName;
            return this;
        }

        public CreateUserRequestDtoBuilder gender(String genderName) {
            this.genderName = genderName;
            return this;
        }

        public CreateUserRequestDtoBuilder ageGroup(String ageGroupName) {
            this.ageGroupName = ageGroupName;
            return this;
        }

        public CreateUserRequestDto get() {
            if (isEmpty) {
                return CreateUserRequestDto.builder().build();
            }
            return CreateUserRequestDto.builder()
                    .username("username" + seed)
                    .fullName("User Full Name")
                    .email("username" + seed + "@email.com")
                    .phone("+312300000" + seed)
                    .password("Password" + seed + "!")
                    .confirmPassword("Password" + seed + "!")
                    .role(roleName)
                    .gender(genderName)
                    .ageGroup(ageGroupName)
                    .build();
        }
    }

    public LoginRequestDtoBuilder loginRequestDto() {
        return new LoginRequestDtoBuilder();
    }

    public static class LoginRequestDtoBuilder {
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

        public LoginRequestDto get() {
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
}
