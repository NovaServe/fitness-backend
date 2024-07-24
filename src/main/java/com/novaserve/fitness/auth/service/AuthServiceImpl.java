/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.auth.service;

import com.novaserve.fitness.auth.dto.LoginProcess;
import com.novaserve.fitness.auth.dto.LoginReqDto;
import com.novaserve.fitness.auth.dto.ValidateTokenResDto;
import com.novaserve.fitness.exception.ExMessage;
import com.novaserve.fitness.exception.ServerEx;
import com.novaserve.fitness.security.auth.JwtTokenProvider;
import com.novaserve.fitness.users.model.User;
import com.novaserve.fitness.users.repository.UserRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {
  private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

  @Autowired AuthenticationManager authenticationManager;

  @Autowired JwtTokenProvider jwtTokenProvider;

  @Autowired AuthUtil authUtil;

  @Autowired UserRepository userRepository;

  @Override
  @Transactional
  public LoginProcess login(LoginReqDto reqDto) {
    try {
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  reqDto.getUsernameOrEmailOrPhone(), reqDto.getPassword()));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      String token = jwtTokenProvider.generateToken(authentication);
      String cookieExpires = authUtil.formatCookieExpire(jwtTokenProvider.getExpireFromJwt(token));
      User user = authUtil.getUserFromAuth(authentication);
      logger.info("Login successful: " + reqDto.getUsernameOrEmailOrPhone());
      return LoginProcess.builder()
          .token(token)
          .role(user.getRole().getName())
          .cookieExpires(cookieExpires)
          .fullName(user.getFullName())
          .build();
    } catch (Exception e) {
      logger.error("Login failed: " + reqDto.getUsernameOrEmailOrPhone());
      logger.error("Login error: " + e.getMessage());
      throw new ServerEx(ExMessage.INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
    }
  }

  @Override
  @Transactional
  public ValidateTokenResDto validateToken() {
    Long userId =
        authUtil.getUserIdFromAuth(SecurityContextHolder.getContext().getAuthentication());
    if (userId != null) {
      Optional<User> userOptional = userRepository.findById(userId);
      if (userOptional.isEmpty()) {
        logger.error("Token is not validated, user with id {} not found", userId);
        return null;
      }
      User user = userOptional.get();
      logger.info("Token validated for {}", user.getUsername());
      return ValidateTokenResDto.builder()
          .fullName(user.getFullName())
          .role(user.getRole().getName())
          .build();
    }
    logger.error("Token is not validated, userId is null");
    return null;
  }
}
