/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.auth.controller;

import com.novaserve.fitness.auth.dto.request.LoginRequestDto;
import com.novaserve.fitness.auth.dto.request.ValidateTokenResponseDto;
import com.novaserve.fitness.auth.dto.response.LoginResponseDto;
import com.novaserve.fitness.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.basePath}/${api.version}/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    private final String apiPath;

    public AuthController(AuthService authService, @Value("${api.basePath}/${api.version}") String apiPath) {
        this.authService = authService;
        this.apiPath = apiPath;
    }

    @Operation(summary = "Login")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto) {
        LoginResponseDto loginResponseDto = authService.login(requestDto);
        ResponseEntity<LoginResponseDto> responseEntity = ResponseEntity.ok(loginResponseDto);
        return responseEntity;
    }

    @Operation(summary = "Validate token")
    @GetMapping("/validate")
    public ResponseEntity<ValidateTokenResponseDto> validateToken() {
        ValidateTokenResponseDto validateTokenResponseDto = authService.validateToken();
        ResponseEntity<ValidateTokenResponseDto> responseEntity = ResponseEntity.ok(validateTokenResponseDto);
        return responseEntity;
    }
}
