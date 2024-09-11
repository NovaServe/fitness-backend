/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.novaserve.fitness.auth.service.AuthUtil;
import com.novaserve.fitness.exception.ExceptionMessage;
import com.novaserve.fitness.exception.ServerException;
import com.novaserve.fitness.helpers.DtoHelper;
import com.novaserve.fitness.helpers.MockHelper;
import com.novaserve.fitness.users.dto.CreateUserRequestDto;
import com.novaserve.fitness.users.model.Role;
import com.novaserve.fitness.users.model.User;
import com.novaserve.fitness.users.repository.UserRepository;
import com.novaserve.fitness.users.service.impl.UserServiceImpl;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class CreateUserTest {
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    AuthUtil authUtil;

    @Mock
    UserRepository userRepository;

    @Spy
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Spy
    MockHelper helper;

    @Spy
    DtoHelper dtoHelper;

    @BeforeEach
    public void beforeEach() {
        lenient().when(userRepository.save(any(User.class))).then(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1000L);
            return user;
        });
    }

    @Test
    void createUser_shouldCreateAdmin_whenSuperadminRequests() {
        User superadmin = helper.user().seed(1).role(Role.ROLE_SUPERADMIN).build();

        CreateUserRequestDto dto =
                dtoHelper.createUserRequestDto().seed(2).role(Role.ROLE_ADMIN).build();

        when(authUtil.getUserFromAuth(any())).thenReturn(Optional.ofNullable(superadmin));
        when(userRepository.findByUsernameOrEmailOrPhone(any(), any(), any())).thenReturn(Optional.empty());

        User actual = userService.createUser(dto);
        assertHelper(actual, dto);
    }

    @ParameterizedTest
    @MethodSource("methodParams_createUser_shouldCreateCustomerOrInstructor_whenAdminRequests")
    void createUser_shouldCreateCustomerOrInstructor_whenAdminRequests(Role role) {
        User admin = helper.user().seed(1).role(Role.ROLE_ADMIN).build();

        CreateUserRequestDto dto =
                dtoHelper.createUserRequestDto().seed(2).role(role).build();

        when(authUtil.getUserFromAuth(any())).thenReturn(Optional.ofNullable(admin));
        when(userRepository.findByUsernameOrEmailOrPhone(any(), any(), any())).thenReturn(Optional.empty());

        User actual = userService.createUser(dto);
        assertHelper(actual, dto);
    }

    static Stream<Arguments> methodParams_createUser_shouldCreateCustomerOrInstructor_whenAdminRequests() {
        return Stream.of(Arguments.of(Role.ROLE_CUSTOMER), Arguments.of(Role.ROLE_INSTRUCTOR));
    }

    @ParameterizedTest
    @MethodSource("methodParams_createUser_shouldThrowException_whenRolesMismatch")
    void createUser_shouldThrowException_whenRolesMismatch(Role principalRole, Role newUserRole) {
        User user = helper.user().seed(1).role(principalRole).build();

        CreateUserRequestDto dto =
                dtoHelper.createUserRequestDto().seed(2).role(newUserRole).build();

        when(authUtil.getUserFromAuth(any())).thenReturn(Optional.ofNullable(user));

        ServerException actual = assertThrows(ServerException.class, () -> userService.createUser(dto));
        assertEquals(actual.getMessage(), ExceptionMessage.ROLES_MISMATCH.getName());
        assertEquals(actual.getStatus(), HttpStatus.BAD_REQUEST);
    }

    static Stream<Arguments> methodParams_createUser_shouldThrowException_whenRolesMismatch() {
        return Stream.of(
                Arguments.of(Role.ROLE_SUPERADMIN, Role.ROLE_SUPERADMIN),
                Arguments.of(Role.ROLE_SUPERADMIN, Role.ROLE_CUSTOMER),
                Arguments.of(Role.ROLE_SUPERADMIN, Role.ROLE_INSTRUCTOR),
                Arguments.of(Role.ROLE_ADMIN, Role.ROLE_ADMIN),
                Arguments.of(Role.ROLE_ADMIN, Role.ROLE_SUPERADMIN),
                Arguments.of(Role.ROLE_CUSTOMER, Role.ROLE_CUSTOMER),
                Arguments.of(Role.ROLE_CUSTOMER, Role.ROLE_SUPERADMIN),
                Arguments.of(Role.ROLE_CUSTOMER, Role.ROLE_ADMIN),
                Arguments.of(Role.ROLE_CUSTOMER, Role.ROLE_INSTRUCTOR),
                Arguments.of(Role.ROLE_INSTRUCTOR, Role.ROLE_INSTRUCTOR),
                Arguments.of(Role.ROLE_INSTRUCTOR, Role.ROLE_INSTRUCTOR),
                Arguments.of(Role.ROLE_INSTRUCTOR, Role.ROLE_SUPERADMIN),
                Arguments.of(Role.ROLE_INSTRUCTOR, Role.ROLE_ADMIN));
    }

    @Test
    void createUser_shouldThrowException_whenUserAlreadyExists() {
        User user = helper.user().seed(1).role(Role.ROLE_ADMIN).build();

        User alreadyExists = helper.user().seed(2).role(Role.ROLE_CUSTOMER).build();

        CreateUserRequestDto dto = dtoHelper
                .createUserRequestDto()
                .seed(2)
                .role(Role.ROLE_CUSTOMER)
                .build();

        when(authUtil.getUserFromAuth(any())).thenReturn(Optional.ofNullable(user));
        when(userRepository.findByUsernameOrEmailOrPhone(any(), any(), any()))
                .thenReturn(Optional.ofNullable(alreadyExists));

        ServerException actual = assertThrows(ServerException.class, () -> userService.createUser(dto));
        assertEquals(actual.getMessage(), ExceptionMessage.ALREADY_EXISTS.getName());
        assertEquals(actual.getStatus(), HttpStatus.BAD_REQUEST);
    }

    void assertHelper(User actual, CreateUserRequestDto dto) {
        String[] comparatorIgnoreFields = new String[] {"id", "assignments", "instructorTrainings"};
        BiPredicate<String, String> passwordBiPredicate = (encoded, raw) -> passwordEncoder.matches(raw, encoded);

        assertThat(actual)
                .usingRecursiveComparison()
                .withEqualsForFields(passwordBiPredicate, "password")
                .ignoringFields(comparatorIgnoreFields)
                .isEqualTo(dto);
        assertNotNull(actual.getId());
    }
}
