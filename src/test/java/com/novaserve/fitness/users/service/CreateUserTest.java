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
import com.novaserve.fitness.users.model.AgeGroup;
import com.novaserve.fitness.users.model.Gender;
import com.novaserve.fitness.users.model.Role;
import com.novaserve.fitness.users.model.User;
import com.novaserve.fitness.users.repository.AgeGroupRepository;
import com.novaserve.fitness.users.repository.GenderRepository;
import com.novaserve.fitness.users.repository.RoleRepository;
import com.novaserve.fitness.users.repository.UserRepository;
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

    @BeforeEach
    public void beforeEach() {
        superadminRole = helper.superadminRole();
        adminRole = helper.adminRole();
        customerRole = helper.customerRole();
        instructorRole = helper.instructorRole();
        gender = helper.female();
        ageGroup = helper.adult();

        lenient().when(genderRepository.findByName(gender.getName())).thenReturn(Optional.of(gender));
        lenient().when(ageGroupRepository.findByName(ageGroup.getName())).thenReturn(Optional.of(ageGroup));
        when(roleRepository.findByName(adminRole.getName())).thenReturn(Optional.of(adminRole));
        when(roleRepository.findByName(customerRole.getName())).thenReturn(Optional.of(customerRole));
        lenient().when(roleRepository.findByName(instructorRole.getName())).thenReturn(Optional.of(instructorRole));
        lenient().when(userRepository.save(any(User.class))).then(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1000L);
            return user;
        });
    }

    void assertHelper(User actual, CreateUserRequestDto dto) {
        String[] comparatorIgnoreFields = new String[] {"id"};
        BiPredicate<String, String> passwordBiPredicate = (encoded, raw) -> passwordEncoder.matches(raw, encoded);
        BiPredicate<Gender, String> genderBiPredicate = (gender, genderName) -> genderName.equals(gender.getName());
        BiPredicate<AgeGroup, String> ageGroupBiPredicate =
                (ageGroup, ageGroupName) -> ageGroupName.equals(ageGroup.getName());
        BiPredicate<Role, String> roleBiPredicate = (role, roleName) -> roleName.equals(role.getName());
        assertThat(actual)
                .usingRecursiveComparison()
                .withEqualsForFields(passwordBiPredicate, "password")
                .withEqualsForFields(genderBiPredicate, "gender")
                .withEqualsForFields(ageGroupBiPredicate, "ageGroup")
                .withEqualsForFields(roleBiPredicate, "role")
                .ignoringFields(comparatorIgnoreFields)
                .isEqualTo(dto);
        assertNotNull(actual.getId());
    }

    @Test
    void createUser_shouldCreateAdmin_whenSuperadminRequests() {
        User superadmin = helper.user()
                .seed(1)
                .role(superadminRole)
                .gender(gender)
                .ageGroup(ageGroup)
                .get();

        CreateUserRequestDto dto = dtoHelper
                .createUserRequestDto()
                .seed(2)
                .role(adminRole.getName())
                .gender(gender.getName())
                .ageGroup(ageGroup.getName())
                .get();

        when(authUtil.getUserFromAuth(any())).thenReturn(Optional.ofNullable(superadmin));
        when(userRepository.findByUsernameOrEmailOrPhone(any(), any(), any())).thenReturn(Optional.empty());

        User actual = userService.createUser(dto);
        assertHelper(actual, dto);
    }

    @ParameterizedTest
    @MethodSource("methodParams_createUser_shouldCreateCustomerOrInstructor_whenAdminRequests")
    void createUser_shouldCreateCustomerOrInstructor_whenAdminRequests(String roleName) {
        User admin = helper.user()
                .seed(1)
                .role(adminRole)
                .gender(gender)
                .ageGroup(ageGroup)
                .get();

        CreateUserRequestDto dto = dtoHelper
                .createUserRequestDto()
                .seed(2)
                .role(roleName)
                .gender(gender.getName())
                .ageGroup(ageGroup.getName())
                .get();

        when(authUtil.getUserFromAuth(any())).thenReturn(Optional.ofNullable(admin));
        when(userRepository.findByUsernameOrEmailOrPhone(any(), any(), any())).thenReturn(Optional.empty());

        User actual = userService.createUser(dto);
        assertHelper(actual, dto);
    }

    static Stream<Arguments> methodParams_createUser_shouldCreateCustomerOrInstructor_whenAdminRequests() {
        return Stream.of(Arguments.of("ROLE_CUSTOMER"), Arguments.of("ROLE_INSTRUCTOR"));
    }

    @ParameterizedTest
    @MethodSource("methodParams_createUser_shouldThrowException_whenRolesMismatch")
    void createUser_shouldThrowException_whenRolesMismatch(String creatorRoleName, String createdRoleName) {
        User user = helper.user()
                .seed(1)
                .role(getRole(creatorRoleName))
                .gender(gender)
                .ageGroup(ageGroup)
                .get();

        CreateUserRequestDto dto = dtoHelper
                .createUserRequestDto()
                .seed(2)
                .role(createdRoleName)
                .gender(gender.getName())
                .ageGroup(ageGroup.getName())
                .get();

        when(authUtil.getUserFromAuth(any())).thenReturn(Optional.ofNullable(user));

        ServerException actual = assertThrows(ServerException.class, () -> userService.createUser(dto));
        assertEquals(actual.getMessage(), ExceptionMessage.ROLES_MISMATCH.getName());
        assertEquals(actual.getStatus(), HttpStatus.BAD_REQUEST);
    }

    static Stream<Arguments> methodParams_createUser_shouldThrowException_whenRolesMismatch() {
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

    @Test
    void createUser_shouldThrowException_whenUserAlreadyExists() {
        User user = helper.user()
                .seed(1)
                .role(adminRole)
                .gender(gender)
                .ageGroup(ageGroup)
                .get();

        User alreadyExists = helper.user()
                .seed(2)
                .role(customerRole)
                .gender(gender)
                .ageGroup(ageGroup)
                .get();

        CreateUserRequestDto dto = dtoHelper
                .createUserRequestDto()
                .seed(2)
                .role(customerRole.getName())
                .gender(gender.getName())
                .ageGroup(ageGroup.getName())
                .get();

        when(authUtil.getUserFromAuth(any())).thenReturn(Optional.ofNullable(user));
        when(userRepository.findByUsernameOrEmailOrPhone(any(), any(), any()))
                .thenReturn(Optional.ofNullable(alreadyExists));

        ServerException actual = assertThrows(ServerException.class, () -> userService.createUser(dto));
        assertEquals(actual.getMessage(), ExceptionMessage.ALREADY_EXISTS.getName());
        assertEquals(actual.getStatus(), HttpStatus.BAD_REQUEST);
    }

    Role getRole(String roleName) {
        return switch (roleName) {
            case "ROLE_SUPERADMIN" -> superadminRole;
            case "ROLE_ADMIN" -> adminRole;
            case "ROLE_CUSTOMER" -> customerRole;
            case "ROLE_INSTRUCTOR" -> instructorRole;
            default -> throw new IllegalStateException("Unexpected value: " + roleName);
        };
    }
}
