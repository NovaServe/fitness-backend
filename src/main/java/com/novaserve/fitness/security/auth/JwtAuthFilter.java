/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.security.auth;

import static com.novaserve.fitness.exceptions.ExceptionMessage.INVALID_CREDENTIALS;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaserve.fitness.exceptions.ExceptionDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Stream;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final CustomUserDetails customUserDetails;

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthFilter(CustomUserDetails customUserDetails, JwtTokenProvider jwtTokenProvider) {
        this.customUserDetails = customUserDetails;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilterInternal(
            HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException {
        String token = getJwtFromRequestCookie(httpServletRequest);

        if (token != null) {
            if (jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsernameFromJwt(token);
                UserDetails userDetails = customUserDetails.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, userDetails.getPassword(), userDetails.getAuthorities());

                usernamePasswordAuthenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } else {
                httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);

                PrintWriter out = httpServletResponse.getWriter();
                out.write(new ObjectMapper().writeValueAsString(new ExceptionDto(INVALID_CREDENTIALS.getName())));
                out.flush();
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    @Deprecated
    private String getJwtFromRequestHeader(HttpServletRequest httpServletRequest) {
        if (httpServletRequest.getHeader("Authorization") == null) {
            return null;
        }
        String token = httpServletRequest.getHeader("Authorization").substring(7);
        return token;
    }

    private String getJwtFromRequestCookie(HttpServletRequest httpServletRequest) {
        Cookie[] cookies = httpServletRequest.getCookies();

        if (cookies != null && cookies.length > 0) {
            String token = Stream.of(cookies)
                    .filter(elt -> "token".equals(elt.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
            return token;
        }
        return null;
    }
}
