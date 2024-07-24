/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.security.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaserve.fitness.exception.ExceptionDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthEntry implements AuthenticationEntryPoint {
  @Override
  public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException e)
      throws IOException {
    res.setStatus(HttpStatus.UNAUTHORIZED.value());
    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
    ExceptionDto dto = new ExceptionDto(e.getMessage());
    PrintWriter out = res.getWriter();
    ObjectMapper objectMapper = new ObjectMapper();
    out.write(objectMapper.writeValueAsString(dto));
    out.flush();
    // Alt
    // res.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
  }
}
