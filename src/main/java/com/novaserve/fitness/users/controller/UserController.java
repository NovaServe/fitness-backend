/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.controller;

import com.novaserve.fitness.users.dto.CreateUserRequestDto;
import com.novaserve.fitness.users.dto.UserResponseDto;
import com.novaserve.fitness.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Operation(summary = "Get user details")
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ROLE_SUPERADMIN','ROLE_ADMIN','ROLE_INSTRUCTOR','ROLE_CUSTOMER')")
  public ResponseEntity<UserResponseDto> getUserDetails(@PathVariable Long id) {
    UserResponseDto responseDto = userService.getUserDetails(id);
    return ResponseEntity.ok(responseDto);
  }
}
