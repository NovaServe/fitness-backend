/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.auth.service;

import com.novaserve.fitness.auth.dto.LoginProcessDto;
import com.novaserve.fitness.auth.dto.LoginRequestDto;
import com.novaserve.fitness.auth.dto.ValidateTokenResponseDto;
import com.novaserve.fitness.exception.ExceptionMessage;
import com.novaserve.fitness.exception.ServerException;
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
  public LoginProcessDto login(LoginRequestDto requestDto) {
    try {
      Authentication auth =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  requestDto.getUsernameOrEmailOrPhone(), requestDto.getPassword()));
      SecurityContextHolder.getContext().setAuthentication(auth);
      String token = jwtTokenProvider.generateToken(auth);
      String cookieExpires =
          authUtil.formatCookieExpires(jwtTokenProvider.getExpiresFromJwt(token));
      User user = authUtil.getUserFromAuth(auth);
      logger.info("Login successful: " + requestDto.getUsernameOrEmailOrPhone());
      return LoginProcessDto.builder()
          .token(token)
          .role(user.getRole().getName())
          .cookieExpires(cookieExpires)
          .fullName(user.getFullName())
          .build();
    } catch (Exception e) {
      logger.error("Login failed: " + requestDto.getUsernameOrEmailOrPhone());
      logger.error("Login error: " + e.getMessage());
      throw new ServerException(ExceptionMessage.INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
    }
  }

  @Override
  @Transactional
  public ValidateTokenResponseDto validateToken() {
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
      return ValidateTokenResponseDto.builder()
          .fullName(user.getFullName())
          .role(user.getRole().getName())
          .build();
    }
    logger.error("Token is not validated, userId is null");
    return null;
  }
}
