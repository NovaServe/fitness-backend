/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.dto.request;

import com.novaserve.fitness.profiles.model.AgeGroup;
import com.novaserve.fitness.profiles.model.Gender;
import com.novaserve.fitness.profiles.model.Role;
import java.util.Set;

public class UsersFilterDto {
    private String fullName;

    private String email;

    private String phone;

    private Set<Role> roles;

    private Set<Gender> genders;

    private Set<AgeGroup> ageGroups;

    private Boolean isActive;

    private Set<Long> clubsIds; // only for Employee (extends UserBase). In Employee Set<Club> is M2M field

    private Set<Long>
            subscriptionsIds; // only for Customer (extends UserBase). In Customer SubscriptionPlan is M2O field
}
