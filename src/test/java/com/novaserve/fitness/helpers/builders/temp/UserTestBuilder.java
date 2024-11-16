/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.helpers.builders.temp;
//
// import com.novaserve.fitness.helpers.DbHelper;
// import com.novaserve.fitness.helpers.Util;
// import com.novaserve.fitness.users.model.User;
// import com.novaserve.fitness.users.model.AgeGroup;
// import com.novaserve.fitness.users.model.Gender;
// import com.novaserve.fitness.users.model.Role;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
//
// public class UserTestBuilder<T> {
//    private Integer seed;
//    private String username;
//    private String fullName;
//    private String email;
//    private String phone;
//    private String password;
//    private Role role;
//    private Gender gender;
//    private AgeGroup ageGroup;
//    private T callerInstance;
//
//    public UserTestBuilder() {}
//
//    public UserTestBuilder(T callerInstance) {
//        this.callerInstance = callerInstance;
//    }
//
//    public UserTestBuilder<T> seed(int seed) {
//        this.seed = seed;
//        return this;
//    }
//
//    public UserTestBuilder<T> username(String username) {
//        this.username = username;
//        return this;
//    }
//
//    public UserTestBuilder<T> fullName(String fullName) {
//        this.fullName = fullName;
//        return this;
//    }
//
//    public UserTestBuilder<T> email(String email) {
//        this.email = email;
//        return this;
//    }
//
//    public UserTestBuilder<T> phone(String phone) {
//        this.phone = phone;
//        return this;
//    }
//
//    public UserTestBuilder<T> password(String rawPassword) {
//
//        return this;
//    }
//
//    public UserTestBuilder<T> role(Role role) {
//        this.role = role;
//        return this;
//    }
//
//    public UserTestBuilder<T> gender(Gender gender) {
//        this.gender = gender;
//        return this;
//    }
//
//    public UserTestBuilder<T> ageGroup(AgeGroup ageGroup) {
//        this.ageGroup = ageGroup;
//        return this;
//    }
//
//    private User instance() {
//        if (seed == null) {
//            throw new IllegalStateException("Seed should be specified");
//        }
//        if (role == null) {
//            throw new IllegalStateException("Role should be specified");
//        }
//
//        return User.builder()
//                .id((long) seed)
//                .username(generateUsernameWithSeed(seed))
//                .fullName(generateFullNameWithSeed(seed))
//                .email(generateEmailWithSeed(seed))
//                .phone(generatePhoneWithSeed(seed))
//                .password(generatePasswordWithSeed(seed))
//                .role(role)
//                .gender(Gender.MALE)
//                .ageGroup(AgeGroup.ADULT)
//                .build();
//    }
//
//    public T build() {
//        if (callerInstance instanceof DbHelper) {
//            ((DbHelper) callerInstance).setUserInstance(instance());
//            return callerInstance;
//        } else {
//            return (T) instance();
//        }
//    }
//
//    private String generateUsernameWithSeed(int seed) {
//        return    "username" + seed;
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
