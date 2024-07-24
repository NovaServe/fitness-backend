/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.auth.controller;

import com.novaserve.fitness.auth.dto.LoginProcess;
import com.novaserve.fitness.auth.dto.LoginReqDto;
import com.novaserve.fitness.auth.dto.LoginResDto;
import com.novaserve.fitness.auth.dto.ValidateTokenResDto;
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
  public ResponseEntity<LoginResDto> login(
      @Valid @RequestBody LoginReqDto reqDto, HttpServletResponse res) {
    LoginProcess process = authService.login(reqDto);
    Cookie cookie = new Cookie("token", process.getToken());
    cookie.setHttpOnly(true);
    cookie.setPath(apiPath);
    cookie.setAttribute("SameSite", "Strict");
    cookie.setAttribute("Expires", process.getCookieExpires());
    res.addCookie(cookie);
    LoginResDto resDto =
        LoginResDto.builder().fullName(process.getFullName()).role(process.getRole()).build();
    return ResponseEntity.ok(resDto);
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
  public ResponseEntity<ValidateTokenResDto> validateToken(HttpServletResponse res) {
    // todo
    return ResponseEntity.ok(authService.validateToken());
  }
}
