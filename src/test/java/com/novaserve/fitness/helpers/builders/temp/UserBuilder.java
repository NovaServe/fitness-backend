/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.helpers.builders.temp;
//
// import com.novaserve.fitness.helpers.Util;
// import com.novaserve.fitness.users.model.*;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
//
// public class UserBuilder implements IUserTestBuilder<UserBase> {
//    private Integer seed;
//
//    private Role role;
//
//    @Override
//    public void withSeed(int seed) {
//        this.seed = seed;
//    }
//
//    @Override
//    public void withRole(Role role) {
//        this.role = role;
//    }
//
//    @Override
//    public UserBase build() {
//        UserBase user = switch(role) {
//            case ROLE_SUPERADMIN, ROLE_ADMIN -> Employee.builder().build();
//            case ROLE_INSTRUCTOR -> Instructor.builder().build();
//            case ROLE_CUSTOMER -> Customer.builder().build();
//        };
//
//        if (seed != null) {
//            user.setId((long) seed);
//            user.setUsername(generateUsernameWithSeed(seed));
//            user.setFullName(generateFullNameWithSeed(seed));
//            user.setEmail(generateEmailWithSeed(seed));
//            user.setPassword(generatePasswordWithSeed(seed));
//        }
//
//        return user;
//    }
//
//    private String generateUsernameWithSeed(int seed) {
//        return "username" + seed;
//    }
//
//    private String generateFullNameWithSeed(int seed) {
//        return "User Full Name " + Util.getNumberName(seed);
//    }
//
//    private String generateEmailWithSeed(int seed) {
//        return "username" + seed + "@email.com";
//    }
//
//    private String generatePhoneWithSeed(int seed) {
//        return "+312300000" + seed;
//    }
//
//    private String generatePasswordWithSeed(int seed) {
//        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        return passwordEncoder.encode("Password" + seed + "!");
//    }
// }
