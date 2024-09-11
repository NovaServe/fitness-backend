/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.controller;

import com.novaserve.fitness.users.dto.request.CreateUserRequestDto;
import com.novaserve.fitness.users.dto.response.UserResponseDto;
import com.novaserve.fitness.users.model.enums.Role;
import com.novaserve.fitness.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.basePath}/${api.version}/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Create user")
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_SUPERADMIN','ROLE_ADMIN')")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequestDto requestDto) {
        userService.createUser(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Get users list")
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_SUPERADMIN','ROLE_ADMIN')")
    public ResponseEntity<Page<UserResponseDto>> getUsers(
            @RequestParam(required = true) List<Role> roles,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String order,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "0") int pageNumber) {
        Page<UserResponseDto> page = userService.getUsers(roles, fullName, sortBy, order, pageSize, pageNumber);
        return ResponseEntity.status(HttpStatus.OK).body(page);
    }

    @Operation(summary = "Get user details")
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ROLE_SUPERADMIN','ROLE_ADMIN','ROLE_INSTRUCTOR','ROLE_CUSTOMER')")
    public ResponseEntity<UserResponseDto> getUserDetails(@PathVariable Long userId) {
        UserResponseDto userResponseDto = userService.getUserDetails(userId);
        return ResponseEntity.ok(userResponseDto);
    }
}
