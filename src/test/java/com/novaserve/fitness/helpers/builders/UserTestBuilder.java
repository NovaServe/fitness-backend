/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.helpers.builders;

import com.novaserve.fitness.helpers.DbHelper;
import com.novaserve.fitness.helpers.Util;
import com.novaserve.fitness.users.model.User;
import com.novaserve.fitness.users.model.enums.AgeGroup;
import com.novaserve.fitness.users.model.enums.Gender;
import com.novaserve.fitness.users.model.enums.Role;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserTestBuilder<T> {
    private int seed;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String password;
    private Role role;
    private Gender gender;
    private AgeGroup ageGroup;
    private T callerInstance;

    public UserTestBuilder() {}

    public UserTestBuilder(T callerInstance) {
        this.callerInstance = callerInstance;
    }

    public UserTestBuilder<T> seed(int seed) {
        this.seed = seed;
        return this;
    }

    public UserTestBuilder<T> username(String username) {
        this.username = username;
        return this;
    }

    public UserTestBuilder<T> fullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public UserTestBuilder<T> email(String email) {
        this.email = email;
        return this;
    }

    public UserTestBuilder<T> phone(String phone) {
        this.phone = phone;
        return this;
    }

    public UserTestBuilder<T> password(String rawPassword) {

        return this;
    }

    public UserTestBuilder<T> role(Role role) {
        this.role = role;
        return this;
    }

    public UserTestBuilder<T> gender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public UserTestBuilder<T> ageGroup(AgeGroup ageGroup) {
        this.ageGroup = ageGroup;
        return this;
    }

    private User instance() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder()
                .id((long) seed)
                .username("username" + seed)
                .fullName("User Full Name " + Util.getNumberName(seed))
                .email("username" + seed + "@email.com")
                .phone("+312300000" + seed)
                .password(
                        password == null
                                ? passwordEncoder.encode("Password" + seed + "!")
                                : passwordEncoder.encode(password))
                .role(role == null ? Role.ROLE_ADMIN : role)
                .gender(gender == null ? Gender.Male : gender)
                .ageGroup(ageGroup == null ? AgeGroup.Adult : ageGroup)
                .build();
    }

    public T build() {
        if (callerInstance instanceof DbHelper) {
            ((DbHelper) callerInstance).setUserInstance(instance());
            return callerInstance;
        } else {
            return (T) instance();
        }
    }
}
