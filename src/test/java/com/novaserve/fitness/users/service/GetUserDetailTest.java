/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.novaserve.fitness.auth.service.AuthUtil;
import com.novaserve.fitness.exception.ExceptionMessage;
import com.novaserve.fitness.exception.ServerException;
import com.novaserve.fitness.helpers.DtoHelper;
import com.novaserve.fitness.helpers.MockHelper;
import com.novaserve.fitness.users.dto.UserResponseDto;
import com.novaserve.fitness.users.model.AgeGroup;
import com.novaserve.fitness.users.model.Gender;
import com.novaserve.fitness.users.model.Role;
import com.novaserve.fitness.users.model.User;
import com.novaserve.fitness.users.repository.AgeGroupRepository;
import com.novaserve.fitness.users.repository.GenderRepository;
import com.novaserve.fitness.users.repository.RoleRepository;
import com.novaserve.fitness.users.repository.UserRepository;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
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
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class GetUserDetailTest {

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

    @Mock
    ModelMapper modelMapper;

    @Spy
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Spy
    MockHelper $mock;

    @Spy
    DtoHelper $dto;

    Role superadminRole;
    Role adminRole;
    Role customerRole;
    Role instructorRole;
    Gender gender;
    AgeGroup ageGroup;

    @BeforeEach
    public void beforeEach() {
        superadminRole = $mock.superadminRole();
        adminRole = $mock.adminRole();
        customerRole = $mock.customerRole();
        instructorRole = $mock.instructorRole();
        gender = $mock.female();
        ageGroup = $mock.adult();

        lenient().when(userRepository.save(any(User.class))).then(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1000L);
            return user;
        });
    }

    @ParameterizedTest
    @MethodSource("provideUserDetailParams")
    public void getUserDetail_shouldReturnDto_whenSuperAdminRequest(Long userId, String roleName) {
        var superAdmin = $mock.user()
                .seed(1)
                .role(superadminRole)
                .gender(gender)
                .ageGroup(ageGroup)
                .get();
        superAdmin.setId(1L);

        var user = $mock.user()
                .seed(userId.intValue())
                .role(getRoleHelper(roleName))
                .gender(gender)
                .ageGroup(ageGroup)
                .get();
        user.setId(userId);

        when(authUtil.getPrincipal(any())).thenReturn(Optional.of(superAdmin));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(any(User.class), eq(UserResponseDto.class))).thenReturn(new UserResponseDto());
        UserResponseDto response = userService.getUserDetail(userId);

        Assertions.assertNotNull(response);
        verify(userRepository).findById(userId);
    }

    static Stream<Arguments> provideUserDetailParams() {
        return Stream.of(Arguments.of(1L, "ROLE_SUPERADMIN"), Arguments.of(2L, "ROLE_ADMIN"));
    }

    @ParameterizedTest
    @MethodSource("provideUserDetailsParams")
    void getUserDetail_shouldReturnDto_whenAdminRequest(Long userId, String roleName) {
        var admin = $mock.user()
                .seed(1)
                .role(adminRole)
                .gender(gender)
                .ageGroup(ageGroup)
                .get();
        admin.setId(1L); // Ensure ID is set

        var user = $mock.user()
                .seed(userId.intValue())
                .role(getRoleHelper(roleName))
                .gender(gender)
                .ageGroup(ageGroup)
                .get();
        user.setId(userId); // Ensure ID is set

        when(authUtil.getPrincipal(any())).thenReturn(Optional.of(admin));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(any(User.class), eq(UserResponseDto.class))).thenReturn(new UserResponseDto());
        UserResponseDto response = userService.getUserDetail(userId);

        Assertions.assertNotNull(response);
        verify(userRepository).findById(userId);
    }

    static Stream<Arguments> provideUserDetailsParams() {
        return Stream.of(
                Arguments.of(1L, "ROLE_ADMIN"), Arguments.of(2L, "ROLE_CUSTOMER"), Arguments.of(3L, "ROLE_INSTRUCTOR"));
    }

    @Test
    public void getUserDetail_shouldReturnDto_whenCustomerRequest() {
        var customer = $mock.user()
                .seed(2)
                .role(customerRole)
                .gender(gender)
                .ageGroup(ageGroup)
                .get();
        customer.setId(2L);
        when(authUtil.getPrincipal(any())).thenReturn(Optional.of(customer));
        when(userRepository.findById(2L)).thenReturn(Optional.of(customer));
        when(modelMapper.map(any(User.class), eq(UserResponseDto.class))).thenReturn(new UserResponseDto());

        UserResponseDto response = userService.getUserDetail(2L);

        Assertions.assertNotNull(response);
        verify(userRepository).findById(2L);
    }

    @Test
    public void getUserDetail_shouldReturnDto_whenSInstructorRequest() {
        var instructor = $mock.user()
                .seed(3)
                .role(instructorRole)
                .gender(gender)
                .ageGroup(ageGroup)
                .get();
        instructor.setId(3L);
        when(authUtil.getPrincipal(any())).thenReturn(Optional.of(instructor));
        when(userRepository.findById(3L)).thenReturn(Optional.of(instructor));
        when(modelMapper.map(any(User.class), eq(UserResponseDto.class))).thenReturn(new UserResponseDto());

        UserResponseDto response = userService.getUserDetail(3L);

        Assertions.assertNotNull(response);
        verify(userRepository).findById(3L);
    }

    @ParameterizedTest
    @MethodSource("getUserParams_rolesMismatch")
    void GetUserDetail_shouldThrowException_whenRolesMismatch(String principalRoleName, String userRoleName) {
        var principal = $mock.user()
                .seed(1)
                .role(getRoleHelper(principalRoleName))
                .gender(gender)
                .ageGroup(ageGroup)
                .get();
        principal.setId(1L);

        var user = $mock.user()
                .seed(2)
                .role(getRoleHelper(userRoleName))
                .gender(gender)
                .ageGroup(ageGroup)
                .get();
        user.setId(2L);

        when(authUtil.getPrincipal(any())).thenReturn(Optional.of(principal));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        var actual = assertThrows(ServerException.class, () -> userService.getUserDetail(user.getId()));
        Assertions.assertEquals(actual.getMessage(), ExceptionMessage.ROLES_MISMATCH.getName());
        Assertions.assertEquals(actual.getStatus(), HttpStatus.BAD_REQUEST);
    }

    static Stream<Arguments> getUserParams_rolesMismatch() {
        return Stream.of(
                Arguments.of("ROLE_SUPERADMIN", "ROLE_SUPERADMIN"),
                Arguments.of("ROLE_SUPERADMIN", "ROLE_CUSTOMER"),
                Arguments.of("ROLE_SUPERADMIN", "ROLE_INSTRUCTOR"),
                Arguments.of("ROLE_ADMIN", "ROLE_ADMIN"),
                Arguments.of("ROLE_ADMIN", "ROLE_SUPERADMIN"),
                Arguments.of("ROLE_CUSTOMER", "ROLE_CUSTOMER"),
                Arguments.of("ROLE_CUSTOMER", "ROLE_SUPERADMIN"),
                Arguments.of("ROLE_CUSTOMER", "ROLE_ADMIN"),
                Arguments.of("ROLE_CUSTOMER", "ROLE_INSTRUCTOR"),
                Arguments.of("ROLE_INSTRUCTOR", "ROLE_INSTRUCTOR"),
                Arguments.of("ROLE_INSTRUCTOR", "ROLE_CUSTOMER"),
                Arguments.of("ROLE_INSTRUCTOR", "ROLE_SUPERADMIN"),
                Arguments.of("ROLE_INSTRUCTOR", "ROLE_ADMIN"));
    }

    Role getRoleHelper(String roleName) {
        return switch (roleName) {
            case "ROLE_SUPERADMIN" -> superadminRole;
            case "ROLE_ADMIN" -> adminRole;
            case "ROLE_CUSTOMER" -> customerRole;
            case "ROLE_INSTRUCTOR" -> instructorRole;
            default -> throw new IllegalStateException("Unexpected value: " + roleName);
        };
    }
}
