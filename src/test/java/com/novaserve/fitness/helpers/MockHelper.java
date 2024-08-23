/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.helpers;

import com.novaserve.fitness.helpers.builders.AreaTestBuilder;
import com.novaserve.fitness.trainings.model.Area;
import com.novaserve.fitness.users.model.AgeGroup;
import com.novaserve.fitness.users.model.Gender;
import com.novaserve.fitness.users.model.Role;
import com.novaserve.fitness.users.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class MockHelper {
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserBuilder user() {
        return new UserBuilder();
    }

    public static class UserBuilder {
        private int seed;
        private Role role;
        private Gender gender;
        private AgeGroup ageGroup;

        public UserBuilder seed(int seed) {
            this.seed = seed;
            return this;
        }

        public UserBuilder role(Role role) {
            this.role = role;
            return this;
        }

        public UserBuilder gender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public UserBuilder ageGroup(AgeGroup ageGroup) {
            this.ageGroup = ageGroup;
            return this;
        }

        public User get() {
            return User.builder()
                    .id((long) seed)
                    .username("username" + seed)
                    .email("username" + seed + "@email.com")
                    .phone("+312300000" + seed)
                    .fullName("User Full Name " + Util.getNumberName(seed))
                    .password(passwordEncoder.encode("Password" + seed + "!"))
                    .role(role)
                    .gender(gender)
                    .ageGroup(ageGroup)
                    .build();
        }
    }

    public AreaTestBuilder<Area> area() {
        return new AreaTestBuilder<Area>();
    }
}
