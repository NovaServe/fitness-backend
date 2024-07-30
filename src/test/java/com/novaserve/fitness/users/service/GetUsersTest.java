/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.novaserve.fitness.auth.service.AuthUtil;
import com.novaserve.fitness.helpers.DtoHelper;
import com.novaserve.fitness.helpers.MockHelper;
import com.novaserve.fitness.users.dto.CreateUserRequestDto;
import com.novaserve.fitness.users.model.AgeGroup;
import com.novaserve.fitness.users.model.Gender;
import com.novaserve.fitness.users.model.Role;
import com.novaserve.fitness.users.model.User;
import com.novaserve.fitness.users.repository.AgeGroupRepository;
import com.novaserve.fitness.users.repository.GenderRepository;
import com.novaserve.fitness.users.repository.RoleRepository;
import com.novaserve.fitness.users.repository.UserRepository;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class GetUsersTest {
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    AuthUtil authUtil;

    @Mock
    RoleRepository roleRepository;

    @Mock
    GenderRepository genderRepository;

    @Mock
    AgeGroupRepository ageGroupRepository;

    @Mock
    UserRepository userRepository;

    @Spy
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Spy
    MockHelper helper;

    @Spy
    DtoHelper dtoHelper;

    Role superadminRole;
    Role adminRole;
    Role customerRole;
    Role instructorRole;
    Gender gender;
    AgeGroup ageGroup;
    List<User> users;

    @BeforeEach
    public void beforeEach() {
        superadminRole = helper.superadminRole();
        adminRole = helper.adminRole();
        customerRole = helper.customerRole();
        instructorRole = helper.instructorRole();
        gender = helper.female();
        ageGroup = helper.adult();
        final Map<Integer, Role> ROLE_MAP = Map.of(
                1, superadminRole,
                2, superadminRole,
                3, adminRole,
                4, adminRole,
                5, customerRole,
                6, customerRole,
                7, instructorRole,
                8, instructorRole);
        users = Collections.unmodifiableList(ROLE_MAP.entrySet().stream()
                .map(entry -> helper.user()
                        .seed(entry.getKey())
                        .role(entry.getValue())
                        .gender(gender)
                        .ageGroup(ageGroup)
                        .get())
                .toList());
    }

    void assertHelper(User actual, CreateUserRequestDto dto) {
        String[] comparatorIgnoreFields = new String[] {"id", "password", "role", "ageGroup", "gender"};
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields(comparatorIgnoreFields)
                .isEqualTo(dto);
        assertEquals(actual.getRole().getName(), dto.getRole());
        assertEquals(actual.getAgeGroup().getName(), dto.getAgeGroup());
        assertEquals(actual.getGender().getName(), dto.getGender());
        assertNotNull(actual.getId());
    }
}
