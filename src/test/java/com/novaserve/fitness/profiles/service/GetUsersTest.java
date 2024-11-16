/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.service;
//
// import static org.assertj.core.api.Assertions.assertThat;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.when;
//
// import com.novaserve.fitness.auth.service.AuthUtil;
// import com.novaserve.fitness.exception.ExceptionMessage;
// import com.novaserve.fitness.exception.ServerException;
// import com.novaserve.fitness.helpers.Util;
// import com.novaserve.fitness.users.dto.response.UserResponseDto;
// import com.novaserve.fitness.users.model.*;
// import com.novaserve.fitness.users.repository.UserRepository;
//
// import java.util.*;
// import java.util.function.BiPredicate;
// import java.util.stream.IntStream;
// import java.util.stream.Stream;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.junit.jupiter.params.ParameterizedTest;
// import org.junit.jupiter.params.provider.Arguments;
// import org.junit.jupiter.params.provider.MethodSource;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.Spy;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.modelmapper.ModelMapper;
// import org.springframework.data.domain.*;
// import org.springframework.http.HttpStatus;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
//
// @ExtendWith(MockitoExtension.class)
// public class GetUsersTest {
//    @InjectMocks
//    private UserServiceImpl userService;
//
//    @Mock
//    private AuthUtil authUtil;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Spy
//    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//
//    @Spy
//    private ModelMapper modelMapper;
//
//    @Spy
//    private Util util;
//
//    private List<UserBase> users;
//
//    @BeforeEach
//    public void beforeEach() {
//        users = generateMockUsers();
//    }
//
//    private List<UserBase> generateMockUsers() {
//        Map<Integer, Role> seedsToRolesMapping = Map.of(
//                1, Role.ROLE_SUPERADMIN,
//                2, Role.ROLE_SUPERADMIN,
//                3, Role.ROLE_ADMIN,
//                4, Role.ROLE_ADMIN,
//                5, Role.ROLE_CUSTOMER,
//                6, Role.ROLE_CUSTOMER,
//                7, Role.ROLE_INSTRUCTOR,
//                8, Role.ROLE_INSTRUCTOR);
//
//        List<UserBase> users = seedsToRolesMapping.entrySet().stream()
//                .map(entry -> {
//                    int seed = entry.getKey();
//                    Role role = entry.getValue();
//                    return switch(role) {
//                        case ROLE_SUPERADMIN, ROLE_ADMIN -> Admin.builder()
//                                .username(util.generateUsernameWithSeed(seed))
//                                .fullName(util)
//                                .email(util.generateEmailWithSeed(seed))
//                                .password(passwordEncoder.encode(creationDto.getPassword()))
//                                .role(creationDto.getRole())
//                                .gender(creationDto.getGender())
//                                .ageGroup(creationDto.getAgeGroup())
//                                .clubs(clubs)
//                                .startDate(creationDto.getStartDate())
//                                .build();;
//                        case ROLE_CUSTOMER -> null;
//                        case ROLE_INSTRUCTOR -> null;
//                    }
//                })
//                .toList();
//
//        return users;
//    }
//
//    @ParameterizedTest
//    @MethodSource("methodParams_getUsers_shouldReturnPageFilteredByRoles_whenSuperadminRequest")
//    public void getUsers_shouldReturnPageFilteredByRoles_whenSuperadminRequest(
//            Role principalRole, List<Role> filterByRoles) {
//        UserBase principal = switch(principalRole) {
//            case ROLE_SUPERADMIN, ROLE_ADMIN -> Employee.builder()
//                    .role(principalRole)
//                    .username(util.generateUsernameWithSeed(1))
//                    .fullName(util.generateFullNameWithSeed(1))
//                    .email(util.generateEmailWithSeed(1))
//                    .password(util.generatePasswordWithSeed(1))
//                    .build();
//            case ROLE_INSTRUCTOR -> Instructor.builder()
//                    .role(principalRole)
//                    .username(util.generateUsernameWithSeed(1))
//                    .fullName(util.generateFullNameWithSeed(1))
//                    .email(util.generateEmailWithSeed(1))
//                    .password(util.generatePasswordWithSeed(1))
//                    .build();
//            case ROLE_CUSTOMER -> Customer.builder()
//                    .role(principalRole)
//                    .username(util.generateUsernameWithSeed(1))
//                    .fullName(util.generateFullNameWithSeed(1))
//                    .email(util.generateEmailWithSeed(1))
//                    .password(util.generatePasswordWithSeed(1))
//                    .build();
//        };
//
//        int pageNumber = 0;
//        int pageSize = 4;
//        String sortBy = "id";
//        String order = "ASC";
//        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.fromString(order), sortBy));
//        Page<UserBase> page = new PageImpl<>(getExpectedWithFilter(filterByRoles, null), pageable, users.size());
//
//        when(authUtil.getUserFromAuth(any())).thenReturn(Optional.ofNullable(principal));
//        when(userRepository.getUsers(filterByRoles, null, pageable)).thenReturn(page);
//
//        Page<UserResponseDto> actual = userService.getUsers(filterByRoles, null, sortBy, order, pageSize, pageNumber);
//
//        assertHelper(page.getContent(), actual.getContent());
//    }
//
//    public static Stream<Arguments> methodParams_getUsers_shouldReturnPageFilteredByRoles_whenSuperadminRequest() {
//        return Stream.of(Arguments.of(Role.ROLE_SUPERADMIN, List.of(Role.ROLE_ADMIN)));
//    }
//
//    @ParameterizedTest
//    @MethodSource("methodParams_getUsers_shouldReturnPageFilteredByRoles_whenAdminRequest")
//    public void getUsers_shouldReturnPageFilteredByRoles_whenAdminRequest(
//            Role principalRole, List<Role> filterByRoles) {
//        User principal = helper.user().seed(users.size()).role(principalRole).build();
//        int pageNumber = 0;
//        int pageSize = 4;
//        String sortBy = "id";
//        String order = "ASC";
//        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.fromString(order), sortBy));
//        Page<User> page = new PageImpl<>(getExpectedWithFilter(filterByRoles, null), pageable, users.size());
//
//        when(authUtil.getUserFromAuth(any())).thenReturn(Optional.ofNullable(principal));
//        when(userRepository.getUsers(filterByRoles, null, pageable)).thenReturn(page);
//
//        Page<UserResponseDto> actual = userService.getUsers(filterByRoles, null, sortBy, order, pageSize, pageNumber);
//
//        assertHelper(page.getContent(), actual.getContent());
//    }
//
//    public static Stream<Arguments> methodParams_getUsers_shouldReturnPageFilteredByRoles_whenAdminRequest() {
//        return Stream.of(
//                Arguments.of(Role.ROLE_ADMIN, List.of(Role.ROLE_CUSTOMER)),
//                Arguments.of(Role.ROLE_ADMIN, List.of(Role.ROLE_INSTRUCTOR)),
//                Arguments.of(Role.ROLE_ADMIN, List.of(Role.ROLE_CUSTOMER, Role.ROLE_INSTRUCTOR)));
//    }
//
//    @ParameterizedTest
//    @MethodSource("methodParams_getUsers_shouldReturnPageFilteredByFullName_whenSuperadminRequest")
//    public void getUsers_shouldReturnPageFilteredByFullName_whenSuperadminRequest(
//            Role principalRole, List<Role> filterByRoles, String fullName) {
//        User principal = helper.user().seed(users.size()).role(principalRole).build();
//        int pageNumber = 0;
//        int pageSize = 4;
//        String sortBy = "id";
//        String order = "ASC";
//        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.fromString(order), sortBy));
//        Page<User> page = new PageImpl<>(getExpectedWithFilter(filterByRoles, fullName), pageable, users.size());
//
//        when(authUtil.getUserFromAuth(any())).thenReturn(Optional.ofNullable(principal));
//        when(userRepository.getUsers(filterByRoles, fullName, pageable)).thenReturn(page);
//
//        Page<UserResponseDto> actual =
//                userService.getUsers(filterByRoles, fullName, sortBy, order, pageSize, pageNumber);
//
//        assertHelper(page.getContent(), actual.getContent());
//    }
//
//    public static Stream<Arguments> methodParams_getUsers_shouldReturnPageFilteredByFullName_whenSuperadminRequest() {
//        return Stream.of(
//                Arguments.of(Role.ROLE_SUPERADMIN, List.of(Role.ROLE_ADMIN), "Three"),
//                Arguments.of(Role.ROLE_SUPERADMIN, List.of(Role.ROLE_ADMIN), "Zero"));
//    }
//
//    @ParameterizedTest
//    @MethodSource("methodParams_getUsers_shouldReturnPageFilteredByFullName_whenAdminRequest")
//    public void getUsers_shouldReturnPageFilteredByFullName_whenAdminRequest(
//            Role principalRole, List<Role> filterByRoles, String fullName) {
//        User principal = helper.user().seed(users.size()).role(principalRole).build();
//        int pageNumber = 0;
//        int pageSize = 4;
//        String sortBy = "id";
//        String order = "ASC";
//        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.fromString(order), sortBy));
//        Page<User> page = new PageImpl<>(getExpectedWithFilter(filterByRoles, fullName), pageable, users.size());
//
//        when(authUtil.getUserFromAuth(any())).thenReturn(Optional.ofNullable(principal));
//        when(userRepository.getUsers(filterByRoles, fullName, pageable)).thenReturn(page);
//
//        Page<UserResponseDto> actual =
//                userService.getUsers(filterByRoles, fullName, sortBy, order, pageSize, pageNumber);
//
//        assertHelper(page.getContent(), actual.getContent());
//    }
//
//    public static Stream<Arguments> methodParams_getUsers_shouldReturnPageFilteredByFullName_whenAdminRequest() {
//        return Stream.of(
//                Arguments.of(Role.ROLE_ADMIN, List.of(Role.ROLE_CUSTOMER), "Five"),
//                Arguments.of(Role.ROLE_ADMIN, List.of(Role.ROLE_INSTRUCTOR), "Seven"),
//                Arguments.of(Role.ROLE_ADMIN, List.of(Role.ROLE_CUSTOMER, Role.ROLE_INSTRUCTOR), "Seven"),
//                Arguments.of(Role.ROLE_ADMIN, List.of(Role.ROLE_CUSTOMER, Role.ROLE_INSTRUCTOR), "Zero"));
//    }
//
//    @ParameterizedTest
//    @MethodSource("methodParams_getUsers_shouldThrowException_whenSuperadminRequest_rolesMismatch")
//    public void getUsers_shouldThrowException_whenSuperadminRequest_rolesMismatch(
//            Role principalRole, List<Role> filterByRoles) {
//        User principal = helper.user().seed(users.size()).role(principalRole).build();
//        int pageNumber = 0;
//        int pageSize = 4;
//        String sortBy = "id";
//        String order = "ASC";
//
//        when(authUtil.getUserFromAuth(any())).thenReturn(Optional.ofNullable(principal));
//
//        ServerException actual = assertThrows(
//                ServerException.class,
//                () -> userService.getUsers(filterByRoles, null, sortBy, order, pageSize, pageNumber));
//
//        assertEquals(HttpStatus.BAD_REQUEST.value(), actual.getStatusCode());
//        assertEquals(ExceptionMessage.ROLES_MISMATCH.getName(), actual.getMessage());
//    }
//
//    public static Stream<Arguments> methodParams_getUsers_shouldThrowException_whenSuperadminRequest_rolesMismatch() {
//        return Stream.of(
//                Arguments.of(Role.ROLE_SUPERADMIN, List.of(Role.ROLE_SUPERADMIN)),
//                Arguments.of(Role.ROLE_SUPERADMIN, List.of(Role.ROLE_CUSTOMER)),
//                Arguments.of(Role.ROLE_SUPERADMIN, List.of(Role.ROLE_INSTRUCTOR)));
//    }
//
//    @ParameterizedTest
//    @MethodSource("methodParams_getUsers_shouldThrowException_whenAdminRequest_rolesMismatch")
//    public void getUsers_shouldThrowException_whenAdminRequest_rolesMismatch(
//            Role principalRole, List<Role> filterByRoles) {
//        User principal = helper.user().seed(users.size()).role(principalRole).build();
//        int pageNumber = 0;
//        int pageSize = 4;
//        String sortBy = "id";
//        String order = "ASC";
//
//        when(authUtil.getUserFromAuth(any())).thenReturn(Optional.ofNullable(principal));
//
//        ServerException actual = assertThrows(
//                ServerException.class,
//                () -> userService.getUsers(filterByRoles, null, sortBy, order, pageSize, pageNumber));
//
//        assertEquals(HttpStatus.BAD_REQUEST.value(), actual.getStatusCode());
//        assertEquals(ExceptionMessage.ROLES_MISMATCH.getName(), actual.getMessage());
//    }
//
//    public static Stream<Arguments> methodParams_getUsers_shouldThrowException_whenAdminRequest_rolesMismatch() {
//        return Stream.of(
//                Arguments.of(Role.ROLE_ADMIN, List.of(Role.ROLE_ADMIN)),
//                Arguments.of(Role.ROLE_ADMIN, List.of(Role.ROLE_SUPERADMIN)));
//    }
//
//    private void assertHelper(List<User> expected, List<UserResponseDto> actual) {
//        assertEquals(expected.size(), actual.size());
//
//        BiPredicate<String, Role> roleBiPredicate = (string, enumeration) -> string.equals(enumeration.name());
//        BiPredicate<String, Gender> genderBiPredicate = (string, enumeration) -> string.equals(enumeration.name());
//        BiPredicate<String, AgeGroup> ageGroupBiPredicate = (string, enumeration) ->
// string.equals(enumeration.name());
//
//        IntStream.range(0, expected.size()).forEach(i -> assertThat(actual.get(i))
//                .usingRecursiveComparison()
//                .withEqualsForFields(roleBiPredicate, "role")
//                .withEqualsForFields(genderBiPredicate, "gender")
//                .withEqualsForFields(ageGroupBiPredicate, "ageGroup")
//                .isEqualTo(expected.get(i)));
//    }
//
//    private List<User> getExpectedWithFilter(List<Role> roles, String fullName) {
//        return users.stream()
//                .filter(user -> roles.contains(user.getRole()))
//                .filter(user -> fullName == null || user.getFullName().contains(fullName))
//                .sorted(Comparator.comparingLong(User::getId))
//                .toList();
//    }
// }
