/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.auth.controller;

import com.novaserve.fitness.auth.dto.LoginProcessData;
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
  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

  @Autowired AuthService authService;

  @Value("${api.basePath}/${api.version}")
  String apiPath;

  @Operation(summary = "Login")
  @PostMapping("/login")
  public ResponseEntity<LoginResponseDto> login(
      @Valid @RequestBody LoginRequestDto requestDto, HttpServletResponse response) {
    LoginProcessData loginProcessData = authService.login(requestDto);
    Cookie cookie = new Cookie("token", loginProcessData.getToken());
    cookie.setHttpOnly(true);
    cookie.setPath(apiPath);
    cookie.setAttribute("SameSite", "Strict");
    cookie.setAttribute("Expires", loginProcessData.getCookieExpires());
    response.addCookie(cookie);
    LoginResponseDto loginResponseDto =
        LoginResponseDto.builder()
            .fullName(loginProcessData.getFullName())
            .role(loginProcessData.getRole())
            .build();
    return ResponseEntity.ok(loginResponseDto);
  }

  @Operation(summary = "Logout")
  @GetMapping("/logout")
  public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
    logger.info("Logout attempt from {}", request.getRemoteAddr());
    Cookie cookie = new Cookie("token", "");
    cookie.setHttpOnly(true);
    cookie.setPath(apiPath);
    cookie.setAttribute("SameSite", "Strict");
    cookie.setMaxAge(0);
    response.addCookie(cookie);
    logger.info("Token cookie is deleted {}", request.getRemoteAddr());
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Validate token")
  @GetMapping("/validate")
  public ResponseEntity<ValidateTokenResponseDto> validateToken(HttpServletResponse response) {
    return ResponseEntity.ok(authService.validateToken());
  }
}
