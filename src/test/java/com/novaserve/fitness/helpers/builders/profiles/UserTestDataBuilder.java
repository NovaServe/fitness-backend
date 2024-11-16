/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.helpers.builders.profiles;

import com.novaserve.fitness.helpers.builders.TestDataBuilder;
import com.novaserve.fitness.profiles.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UserTestDataBuilder<T extends UserBase> implements TestDataBuilder<T> {
    private Integer seed;

    private List<Consumer<T>> consumers;

    private T instance;

    public UserTestDataBuilder(Class<T> userType) {
        switch (userType.getSimpleName()) {
            case "SuperAdmin" -> instance = userType.cast(SuperAdmin.builder().build());
            case "Admin" -> instance = userType.cast(Admin.builder().build());
            case "Instructor" -> instance = userType.cast(Instructor.builder().build());
            case "Customer" -> instance = userType.cast(Customer.builder().build());
        }
    }

    @Override
    public UserTestDataBuilder<T> withSeed(int seed) {
        if (seed < 0 || seed > 10) {
            throw new IllegalArgumentException("Seed should be in between 0 and 10");
        }
        this.seed = seed;
        return this;
    }

    @Override
    public UserTestDataBuilder<T> with(Consumer<T> consumer) {
        if (consumers == null) {
            consumers = new ArrayList<>();
        }
        consumers.add(consumer);
        return this;
    }

    @Override
    public T build() {
        if (seed != null) {
            instance.setUsername(UserUtil.generateUsernameWithSeed(seed));
            instance.setFullName(UserUtil.generateFullNameWithSeed(seed));
            instance.setEmail(UserUtil.generateEmailWithSeed(seed));
            instance.setPhone(UserUtil.generatePhoneWithSeed(seed));
            instance.setPassword(UserUtil.generateHashedPasswordWithSeed(seed));
        }
        if (instance instanceof SuperAdmin superAdmin) {
            superAdmin.setRole(Role.ROLE_SUPERADMIN);
        }
        if (instance instanceof Admin admin) {
            admin.setRole(Role.ROLE_ADMIN);
        }
        if (instance instanceof Instructor instructor) {
            instructor.setRole(Role.ROLE_INSTRUCTOR);
        }
        if (instance instanceof Customer customer) {
            customer.setRole(Role.ROLE_CUSTOMER);
        }

        instance.setActive(true);

        if (consumers != null) {
            consumers.forEach(consumer -> consumer.accept(instance));
        }
        return instance;
    }
}
