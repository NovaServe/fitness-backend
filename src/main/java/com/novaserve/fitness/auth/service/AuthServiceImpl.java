/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.auth.service;

import com.novaserve.fitness.auth.dto.request.LoginRequestDto;
import com.novaserve.fitness.auth.dto.request.ValidateTokenResponseDto;
import com.novaserve.fitness.auth.dto.response.LoginResponseDto;
import com.novaserve.fitness.exceptions.ExceptionMessage;
import com.novaserve.fitness.exceptions.ServerException;
import com.novaserve.fitness.profiles.model.UserBase;
import com.novaserve.fitness.profiles.repository.UserRepository;
import com.novaserve.fitness.security.auth.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final AuthUtil authUtil;

    private final UserRepository userRepository;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            AuthUtil authUtil,
            UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authUtil = authUtil;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public LoginResponseDto login(LoginRequestDto dto) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getUsernameOrEmailOrPhone(), dto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(auth);
            UserBase user = authUtil.getUserFromAuth(auth)
                    .orElseThrow(
                            () -> new ServerException(ExceptionMessage.INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED));

            String token = jwtTokenProvider.generateToken(user);
            logger.info("Logged in: " + dto.getUsernameOrEmailOrPhone());

            LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                    .token(token)
                    .role(user.getRoleName())
                    .fullName(user.getFullName())
                    .build();
            return loginResponseDto;
        } catch (Exception e) {
            logger.error("Login failed: " + dto.getUsernameOrEmailOrPhone());
            logger.error("Login error: " + e.getMessage());
            throw new ServerException(ExceptionMessage.INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    @Transactional
    public ValidateTokenResponseDto validateToken() {
        Long userId =
                authUtil.getUserIdFromAuth(SecurityContextHolder.getContext().getAuthentication());
        if (userId == null) {
            logger.error("Token is not validated, userId is null");
            return null;
        }
        UserBase user = userRepository.findById(userId).orElseThrow(() -> {
            logger.error("Token is not validated, user with id {} not found", userId);
            return new ServerException(ExceptionMessage.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
        });

        ValidateTokenResponseDto validateTokenResponseDto = ValidateTokenResponseDto.builder()
                .fullName(user.getFullName())
                .role(user.getRoleName())
                .build();
        logger.info("Token validated for {}", user.getUsername());
        return validateTokenResponseDto;
    }
}
