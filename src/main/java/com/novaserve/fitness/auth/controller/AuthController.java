/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.auth.controller;

import com.novaserve.fitness.auth.dto.LoginProcessDto;
import com.novaserve.fitness.auth.dto.LoginRequestDto;
import com.novaserve.fitness.auth.dto.LoginResponseDto;
import com.novaserve.fitness.auth.dto.ValidateTokenResponseDto;
import com.novaserve.fitness.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.basePath}/${api.version}/auth")
public class AuthController {
    @Autowired
    AuthService authService;

    @Value("${api.basePath}/${api.version}")
    String apiPath;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Operation(summary = "Login")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto requestDto, HttpServletResponse res) {
        LoginProcessDto processDto = authService.login(requestDto);
        Cookie cookie = new Cookie("token", processDto.getToken());
        cookie.setHttpOnly(true);
        cookie.setPath(apiPath);
        cookie.setAttribute("SameSite", "Strict");
        cookie.setAttribute("Expires", processDto.getCookieExpires());
        res.addCookie(cookie);
        return ResponseEntity.ok(LoginResponseDto.builder()
                .fullName(processDto.getFullName())
                .role(processDto.getRole())
                .build());
    }

    @Operation(summary = "Logout")
    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest req, HttpServletResponse res) {
        logger.info("Logout attempt from {}", req.getRemoteAddr());
        Cookie cookie = new Cookie("token", "");
        cookie.setHttpOnly(true);
        cookie.setPath(apiPath);
        cookie.setAttribute("SameSite", "Strict");
        cookie.setMaxAge(0);
        res.addCookie(cookie);
        logger.info("Token cookie is deleted {}", req.getRemoteAddr());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Validate token")
    @GetMapping("/validate")
    public ResponseEntity<ValidateTokenResponseDto> validateToken() {
        return ResponseEntity.ok(authService.validateToken());
    }
}
