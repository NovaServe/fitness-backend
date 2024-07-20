package com.novaserve.fitness.users.auth.service;

import com.novaserve.fitness.exception.ApiException;
import com.novaserve.fitness.exception.ErrorMessage;
import com.novaserve.fitness.security.auth.JWTTokenProvider;
import com.novaserve.fitness.users.auth.dto.LoginProcessData;
import com.novaserve.fitness.users.auth.dto.LoginRequestDto;
import com.novaserve.fitness.users.auth.dto.ValidateTokenResponseDto;
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

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JWTTokenProvider jwtTokenProvider;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public LoginProcessData login(LoginRequestDto requestDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    requestDto.getUsernameOrEmailOrPhone(), requestDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateToken(authentication);
            String cookieExpires =
                    authUtil.formatCookieExpirationDateTime(jwtTokenProvider.getExpirationDateFromJWT(token));
            User user = authUtil.getUserFromAuthentication(authentication);
            logger.info("Login successful: " + requestDto.getUsernameOrEmailOrPhone());
            return LoginProcessData.builder()
                    .token(token)
                    .role(user.getRole().getName())
                    .cookieExpires(cookieExpires)
                    .fullName(user.getFullName())
                    .build();
        } catch (Exception e) {
            logger.error("Login failed: " + requestDto.getUsernameOrEmailOrPhone());
            logger.error("Login error: " + e.getMessage());
            throw new ApiException(ErrorMessage.INVALID_CREDENTIALS, null, HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    @Transactional
    public ValidateTokenResponseDto validateToken() {
        Long userId = authUtil.getUserIdFromAuthentication(
                SecurityContextHolder.getContext().getAuthentication());
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
