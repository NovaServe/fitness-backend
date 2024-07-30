/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.security.auth;

import static com.novaserve.fitness.exception.ExceptionMessage.INVALID_CREDENTIALS;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaserve.fitness.exception.ExceptionDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Stream;
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
public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired
    CustomUserDetails customUserDetails;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String token = getFwtFromRequestCookie(req);
        if (nonNull(token)) {
            if (jwtTokenProvider.validateToken(token)) {
                var username = jwtTokenProvider.getUsernameFromJwt(token);
                UserDetails userDetails = customUserDetails.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, userDetails.getPassword(), userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } else {
                res.setStatus(HttpStatus.UNAUTHORIZED.value());
                res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                var dto = new ExceptionDto(INVALID_CREDENTIALS.getName());
                PrintWriter out = res.getWriter();
                out.write(new ObjectMapper().writeValueAsString(dto));
                out.flush();
            }
        }
        chain.doFilter(req, res);
    }

    private String getJwtFromRequestHeader(HttpServletRequest req) {
        if (isNull(req.getHeader("Authorization"))) return null;
        return req.getHeader("Authorization").substring(7);
    }

    private String getFwtFromRequestCookie(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (nonNull(cookies) && cookies.length > 0) {
            return Stream.of(cookies)
                    .filter(elt -> "token".equals(elt.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}
