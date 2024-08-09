/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
import com.novaserve.fitness.users.repository.RoleRepository;
import com.novaserve.fitness.users.repository.UserRepository;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
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
    UserRepository userRepository;

    @Spy
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Spy
    ModelMapper modelMapper;

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
        gender = Gender.Female;
        ageGroup = AgeGroup.Adult;
        final Map<Integer, Role> SEED_ROLE_MAP = Map.of(
                0, superadminRole,
                1, superadminRole,
                2, adminRole,
                3, adminRole,
                4, customerRole,
                5, customerRole,
                6, instructorRole,
                7, instructorRole);
        users = Collections.unmodifiableList(SEED_ROLE_MAP.entrySet().stream()
                .map(entry -> helper.user()
                        .seed(entry.getKey())
                        .role(entry.getValue())
                        .gender(gender)
                        .ageGroup(ageGroup)
                        .get())
                .toList());
    }

    @ParameterizedTest
    @MethodSource("methodParams_getUsers_shouldReturnPageFilteredByRoles_whenSuperadminRequest")
    void getUsers_shouldReturnPageFilteredByRoles_whenSuperadminRequest(String principalRoleName, List<String> roles) {
        User principal = helper.user()
                .seed(users.size())
                .role(getRole(principalRoleName))
                .gender(gender)
                .ageGroup(ageGroup)
                .get();
        int pageNumber = 0;
        int pageSize = 4;
        String sortBy = "id";
        String order = "ASC";
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.fromString(order), sortBy));
        Page<User> page = new PageImpl<>(getExpected(roles, null), pageable, users.size());

        when(authUtil.getUserFromAuth(any())).thenReturn(Optional.ofNullable(principal));
        when(userRepository.getUsers(roles, null, pageable)).thenReturn(page);

        Page<UserResponseDto> actual = userService.getUsers(roles, null, sortBy, order, pageSize, pageNumber);
        assertHelper(page.getContent(), actual.getContent(), roles);
    }

    static Stream<Arguments> methodParams_getUsers_shouldReturnPageFilteredByRoles_whenSuperadminRequest() {
        return Stream.of(Arguments.of("ROLE_SUPERADMIN", List.of("ROLE_ADMIN")));
    }

    @ParameterizedTest
    @MethodSource("methodParams_getUsers_shouldReturnPageFilteredByRoles_whenAdminRequest")
    void getUsers_shouldReturnPageFilteredByRoles_whenAdminRequest(String principalRoleName, List<String> roles) {
        User principal = helper.user()
                .seed(users.size())
                .role(getRole(principalRoleName))
                .gender(gender)
                .ageGroup(ageGroup)
                .get();
        int pageNumber = 0;
        int pageSize = 4;
        String sortBy = "id";
        String order = "ASC";
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.fromString(order), sortBy));
        Page<User> page = new PageImpl<>(getExpected(roles, null), pageable, users.size());

        when(authUtil.getUserFromAuth(any())).thenReturn(Optional.ofNullable(principal));
        when(userRepository.getUsers(roles, null, pageable)).thenReturn(page);

        Page<UserResponseDto> actual = userService.getUsers(roles, null, sortBy, order, pageSize, pageNumber);
        assertHelper(page.getContent(), actual.getContent(), roles);
    }

    static Stream<Arguments> methodParams_getUsers_shouldReturnPageFilteredByRoles_whenAdminRequest() {
        return Stream.of(
                Arguments.of("ROLE_ADMIN", List.of("ROLE_CUSTOMER")),
                Arguments.of("ROLE_ADMIN", List.of("ROLE_INSTRUCTOR")),
                Arguments.of("ROLE_ADMIN", List.of("ROLE_CUSTOMER", "ROLE_INSTRUCTOR")));
    }

    @ParameterizedTest
    @MethodSource("methodParams_getUsers_shouldReturnPageFilteredByFullName_whenSuperadminRequest")
    void getUsers_shouldReturnPageFilteredByFullName_whenSuperadminRequest(
            String principalRoleName, List<String> roles) {
        User principal = helper.user()
                .seed(users.size())
                .role(getRole(principalRoleName))
                .gender(gender)
                .ageGroup(ageGroup)
                .get();
        int pageNumber = 0;
        int pageSize = 4;
        String sortBy = "id";
        String order = "ASC";
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.fromString(order), sortBy));
        Page<User> page = new PageImpl<>(getExpected(roles, null), pageable, users.size());

        when(authUtil.getUserFromAuth(any())).thenReturn(Optional.ofNullable(principal));
        when(userRepository.getUsers(roles, null, pageable)).thenReturn(page);

        Page<UserResponseDto> actual = userService.getUsers(roles, null, sortBy, order, pageSize, pageNumber);
        assertHelper(page.getContent(), actual.getContent(), roles);
    }

    static Stream<Arguments> methodParams_getUsers_shouldReturnPageFilteredByFullName_whenSuperadminRequest() {
        return Stream.of(
                Arguments.of("ROLE_SUPERADMIN", List.of("ROLE_ADMIN"), "Three"),
                Arguments.of("ROLE_SUPERADMIN", List.of("ROLE_ADMIN"), "Zero"));
    }

    @ParameterizedTest
    @MethodSource("methodParams_getUsers_shouldReturnPageFilteredByFullName_whenAdminRequest")
    void getUsers_shouldReturnPageFilteredByFullName_whenAdminRequest(String principalRoleName, List<String> roles) {
        User principal = helper.user()
                .seed(users.size())
                .role(getRole(principalRoleName))
                .gender(gender)
                .ageGroup(ageGroup)
                .get();
        int pageNumber = 0;
        int pageSize = 4;
        String sortBy = "id";
        String order = "ASC";
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.fromString(order), sortBy));
        Page<User> page = new PageImpl<>(getExpected(roles, null), pageable, users.size());

        when(authUtil.getUserFromAuth(any())).thenReturn(Optional.ofNullable(principal));
        when(userRepository.getUsers(roles, null, pageable)).thenReturn(page);

        Page<UserResponseDto> actual = userService.getUsers(roles, null, sortBy, order, pageSize, pageNumber);
        assertHelper(page.getContent(), actual.getContent(), roles);
    }

    static Stream<Arguments> methodParams_getUsers_shouldReturnPageFilteredByFullName_whenAdminRequest() {
        return Stream.of(
                Arguments.of("ROLE_ADMIN", List.of("ROLE_CUSTOMER"), "Five"),
                Arguments.of("ROLE_ADMIN", List.of("ROLE_INSTRUCTOR"), "Seven"),
                Arguments.of("ROLE_ADMIN", List.of("ROLE_CUSTOMER", "ROLE_INSTRUCTOR"), "Seven"),
                Arguments.of("ROLE_ADMIN", List.of("ROLE_CUSTOMER", "ROLE_INSTRUCTOR"), "Zero"));
    }

    @ParameterizedTest
    @MethodSource("methodParams_getUsers_shouldThrowException_whenSuperadminRequest_rolesMismatch")
    void getUsers_shouldThrowException_whenSuperadminRequest_rolesMismatch(
            String principalRoleName, List<String> roles) {
        User principal = helper.user()
                .seed(users.size())
                .role(getRole(principalRoleName))
                .gender(gender)
                .ageGroup(ageGroup)
                .get();
        int pageNumber = 0;
        int pageSize = 4;
        String sortBy = "id";
        String order = "ASC";

        when(authUtil.getUserFromAuth(any())).thenReturn(Optional.ofNullable(principal));

        ServerException actual = assertThrows(
                ServerException.class, () -> userService.getUsers(roles, null, sortBy, order, pageSize, pageNumber));
        assertEquals(HttpStatus.BAD_REQUEST.value(), actual.getStatusCode());
        assertEquals(ExceptionMessage.ROLES_MISMATCH.getName(), actual.getMessage());
    }

    static Stream<Arguments> methodParams_getUsers_shouldThrowException_whenSuperadminRequest_rolesMismatch() {
        return Stream.of(
                Arguments.of("ROLE_SUPERADMIN", List.of("ROLE_SUPERADMIN")),
                Arguments.of("ROLE_SUPERADMIN", List.of("ROLE_CUSTOMER")),
                Arguments.of("ROLE_SUPERADMIN", List.of("ROLE_INSTRUCTOR")));
    }

    @ParameterizedTest
    @MethodSource("methodParams_getUsers_shouldThrowException_whenAdminRequest_rolesMismatch")
    void getUsers_shouldThrowException_whenAdminRequest_rolesMismatch(String principalRoleName, List<String> roles) {
        User principal = helper.user()
                .seed(users.size())
                .role(getRole(principalRoleName))
                .gender(gender)
                .ageGroup(ageGroup)
                .get();
        int pageNumber = 0;
        int pageSize = 4;
        String sortBy = "id";
        String order = "ASC";

        when(authUtil.getUserFromAuth(any())).thenReturn(Optional.ofNullable(principal));

        ServerException actual = assertThrows(
                ServerException.class, () -> userService.getUsers(roles, null, sortBy, order, pageSize, pageNumber));
        assertEquals(HttpStatus.BAD_REQUEST.value(), actual.getStatusCode());
        assertEquals(ExceptionMessage.ROLES_MISMATCH.getName(), actual.getMessage());
    }

    static Stream<Arguments> methodParams_getUsers_shouldThrowException_whenAdminRequest_rolesMismatch() {
        return Stream.of(
                Arguments.of("ROLE_ADMIN", List.of("ROLE_ADMIN")),
                Arguments.of("ROLE_ADMIN", List.of("ROLE_SUPERADMIN")));
    }

    void assertHelper(List<User> expected, List<UserResponseDto> actual, List<String> roles) {
        BiPredicate<String, Gender> genderBiPredicate = (genderName, gender) -> genderName.equals(gender.name());
        BiPredicate<String, AgeGroup> ageGroupBiPredicate =
                (ageGroupName, ageGroup) -> ageGroupName.equals(ageGroup.name());
        BiPredicate<String, Role> roleBiPredicate = (roleName_, role) -> roleName_.equals(role.getName());
        assertEquals(expected.size(), actual.size());
        IntStream.range(0, expected.size()).forEach(i -> assertThat(actual.get(i))
                .usingRecursiveComparison()
                .withEqualsForFields(genderBiPredicate, "gender")
                .withEqualsForFields(ageGroupBiPredicate, "ageGroup")
                .withEqualsForFields(roleBiPredicate, "role")
                .isEqualTo(expected.get(i)));
    }

    List<User> getExpected(List<String> roles, String fullName) {
        return users.stream()
                .filter(user -> roles.contains(user.getRoleName()))
                .filter(user -> fullName == null || user.getFullName().contains(fullName))
                .sorted(Comparator.comparingLong(User::getId))
                .toList();
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
