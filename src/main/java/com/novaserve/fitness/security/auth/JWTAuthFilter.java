/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.security.auth;

import static com.novaserve.fitness.exception.ExceptionMessage.INVALID_CREDENTIALS;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaserve.fitness.exception.ExceptionDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JWTAuthFilter extends OncePerRequestFilter {

  @Autowired CustomUserDetailsService customUserDetailsService;

  @Autowired JWTTokenProvider jwtTokenProvider;

  @Override
  public void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String token = getFWTFromRequestCookie(request);
    if (token != null) {
      if (jwtTokenProvider.validateToken(token)) {
        String username = jwtTokenProvider.getUsernameFromJWT(token);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
            new UsernamePasswordAuthenticationToken(
                userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        usernamePasswordAuthenticationToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
      } else {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ExceptionDto exceptionDto = new ExceptionDto(INVALID_CREDENTIALS.getName());
        PrintWriter out = response.getWriter();
        ObjectMapper objectMapper = new ObjectMapper();
        out.write(objectMapper.writeValueAsString(exceptionDto));
        out.flush();
      }
    }
    filterChain.doFilter(request, response);
  }

  private String getJWTFromRequestHeader(HttpServletRequest request) {
    if (request.getHeader("Authorization") == null) return null;
    String bearerToken = request.getHeader("Authorization");
    return bearerToken.substring(7);
  }

  private String getFWTFromRequestCookie(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null && cookies.length > 0) {
      Optional<Cookie> cookie =
          Arrays.stream(request.getCookies())
              .filter(elt -> "token".equals(elt.getName()))
              .findFirst();
      if (cookie.isPresent() && !"".equals(cookie.get().getValue())) {
        return cookie.get().getValue();
      }
      return null;
    }
    return null;
  }
}
