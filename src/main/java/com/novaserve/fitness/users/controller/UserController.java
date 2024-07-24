/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.users.controller;

import com.novaserve.fitness.users.dto.CreateUserReqDto;
import com.novaserve.fitness.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.basePath}/${api.version}/users")
public class UserController {

  @Autowired UserService userService;

  @Operation(summary = "Create user")
  @PostMapping
  @PreAuthorize("hasAnyRole('ROLE_SUPERADMIN','ROLE_ADMIN')")
  public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserReqDto requestDto) {
    userService.createUser(requestDto);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}
