/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.controller;

import com.novaserve.fitness.users.dto.CreateUserRequestDto;
import com.novaserve.fitness.users.dto.UserResponseDto;
import com.novaserve.fitness.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.basePath}/${api.version}/users")
public class UserController {
    @Autowired
    UserService userService;

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
            @RequestParam(required = true) List<String> roles,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String order,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "0") int pageNumber) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getUsers(roles, fullName, sortBy, order, pageSize, pageNumber));
    }
}
