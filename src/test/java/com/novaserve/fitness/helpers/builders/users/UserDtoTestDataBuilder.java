/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.helpers.builders.users;

import com.novaserve.fitness.helpers.builders.TestDataBuilder;
import com.novaserve.fitness.profiles.dto.request.*;
import com.novaserve.fitness.profiles.model.Role;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UserDtoTestDataBuilder<T extends UserCreationBaseDto> implements TestDataBuilder<T> {
    private Integer seed;

    private List<Consumer<T>> consumers;

    private T instance;

    public UserDtoTestDataBuilder(Class<T> userType) {
        switch (userType.getSimpleName()) {
            case "AdminCreationDto" -> instance =
                    userType.cast(AdminCreationDto.builder().build());
            case "InstructorCreationDto" -> instance =
                    userType.cast(InstructorCreationDto.builder().build());
            case "CustomerCreationDto" -> instance =
                    userType.cast(CustomerCreationDto.builder().build());
        }
    }

    @Override
    public UserDtoTestDataBuilder<T> withSeed(int seed) {
        if (seed < 0 || seed > 10) {
            throw new IllegalArgumentException("Seed should be in between 0 and 10");
        }
        this.seed = seed;
        return this;
    }

    @Override
    public UserDtoTestDataBuilder<T> with(Consumer<T> consumer) {
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
            String password = UserUtil.generateRawPasswordWithSeed(seed);
            instance.setPassword(password);
            instance.setConfirmPassword(password);

            if (instance instanceof InstructorCreationDto instructorCreationDto) {
                instructorCreationDto.setBio("Bio " + seed);
            }
        }

        if (instance instanceof AdminCreationDto adminCreationDto) {
            adminCreationDto.setStartDate(LocalDate.now().minusDays(7));
            adminCreationDto.setType(UserCreationDtoType.ADMIN);
            adminCreationDto.setRole(Role.ROLE_ADMIN);
        }
        if (instance instanceof InstructorCreationDto instructorCreationDto) {
            instructorCreationDto.setStartDate(LocalDate.now().minusDays(7));
            instructorCreationDto.setType(UserCreationDtoType.INSTRUCTOR);
            instructorCreationDto.setRole(Role.ROLE_INSTRUCTOR);
        }
        if (instance instanceof CustomerCreationDto customerCreationDto) {
            customerCreationDto.setType(UserCreationDtoType.CUSTOMER);
            customerCreationDto.setRole(Role.ROLE_CUSTOMER);
        }

        if (consumers != null) {
            consumers.forEach(consumer -> consumer.accept(instance));
        }
        return instance;
    }
}
