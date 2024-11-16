/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.service;

import com.novaserve.fitness.exceptions.NoStrategyFound;
import com.novaserve.fitness.profiles.model.Role;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class UserCreationFactory {
    private final AdminCreationStrategyImpl adminCreationStrategy;

    private final InstructorCreationStrategyImpl instructorCreationStrategy;

    private final CustomerCreationStrategyImpl customerCreationStrategy;

    private final Map<Role, UserCreationStrategy> strategies;

    public UserCreationFactory(
            AdminCreationStrategyImpl adminCreationStrategy,
            InstructorCreationStrategyImpl instructorCreationStrategy,
            CustomerCreationStrategyImpl customerCreationStrategy) {
        this.adminCreationStrategy = adminCreationStrategy;
        this.instructorCreationStrategy = instructorCreationStrategy;
        this.customerCreationStrategy = customerCreationStrategy;

        this.strategies = new HashMap<>();
        this.strategies.put(Role.ROLE_SUPERADMIN, adminCreationStrategy);
        this.strategies.put(Role.ROLE_ADMIN, adminCreationStrategy);
        this.strategies.put(Role.ROLE_INSTRUCTOR, instructorCreationStrategy);
        this.strategies.put(Role.ROLE_CUSTOMER, customerCreationStrategy);
    }

    public UserCreationStrategy createStrategyInstance(Role role) {
        UserCreationStrategy strategy = strategies.get(role);
        if (strategy == null) {
            throw new NoStrategyFound(role.name());
        }
        return strategy;
    }
}
