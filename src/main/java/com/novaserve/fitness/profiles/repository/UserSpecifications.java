/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.repository;

import com.novaserve.fitness.profiles.model.UserBase;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecifications {
    public static Specification<UserBase> hasName(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("name"), name);
    }

    public static Specification<UserBase> hasAgeGreaterThan(int age) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("age"), age);
    }
    //
}
