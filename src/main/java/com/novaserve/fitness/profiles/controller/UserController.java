/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.profiles.controller;

import com.novaserve.fitness.profiles.dto.request.CustomerCreationDto;
import com.novaserve.fitness.profiles.dto.request.UserCreationBaseDto;
import com.novaserve.fitness.profiles.dto.response.CustomerDetailsDto;
import com.novaserve.fitness.profiles.dto.response.UserDetailsBaseDto;
import com.novaserve.fitness.profiles.model.Role;
import com.novaserve.fitness.profiles.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
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
    public ResponseEntity<UserDetailsBaseDto> createUser(@Valid @RequestBody UserCreationBaseDto userCreationDto)
            throws NoSuchAlgorithmException {
        UserDetailsBaseDto userDetailsDto = userService.createUser(userCreationDto);
        ResponseEntity<UserDetailsBaseDto> responseEntity =
                ResponseEntity.status(HttpStatus.CREATED).body(userDetailsDto);
        return responseEntity;
    }

    @Operation(summary = "Signup customer")
    @PostMapping("/signup")
    public ResponseEntity<CustomerDetailsDto> signupCustomer(
            @Valid @RequestBody CustomerCreationDto customerCreationDto) throws NoSuchAlgorithmException {
        CustomerDetailsDto userDetailsDto = userService.signupCustomer(customerCreationDto);
        ResponseEntity<CustomerDetailsDto> responseEntity =
                ResponseEntity.status(HttpStatus.CREATED).body(userDetailsDto);
        return responseEntity;
    }

    @Operation(summary = "Get user details")
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ROLE_SUPERADMIN','ROLE_ADMIN','ROLE_INSTRUCTOR','ROLE_CUSTOMER')")
    public ResponseEntity<UserDetailsBaseDto> getUserDetails(@PathVariable Long userId) {
        UserDetailsBaseDto userDetailsBaseDto = userService.getUserDetails(userId);
        ResponseEntity<UserDetailsBaseDto> responseEntity = ResponseEntity.ok(userDetailsBaseDto);
        return responseEntity;
    }

    @Operation(summary = "Get users list")
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_SUPERADMIN','ROLE_ADMIN')")
    public ResponseEntity<Page<UserDetailsBaseDto>> getUsers(
            @RequestParam(required = true) Set<Role> roles,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String orderBy,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "0") int pageNumber) {
        Page<UserDetailsBaseDto> page = userService.getUsers(roles, fullName, sortBy, orderBy, pageSize, pageNumber);
        ResponseEntity<Page<UserDetailsBaseDto>> responseEntity =
                ResponseEntity.status(HttpStatus.OK).body(page);
        return responseEntity;
    }
}
