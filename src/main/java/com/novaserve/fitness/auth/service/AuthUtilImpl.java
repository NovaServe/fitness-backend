/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.auth.service;

import com.novaserve.fitness.users.model.User;
import com.novaserve.fitness.users.repository.UserRepository;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthUtilImpl implements AuthUtil {
  @Autowired UserRepository userRepository;

  @Override
  public Long getUserIdFromAuth(Authentication auth) {
    User user = getUserFromAuth(auth);
    return user == null ? null : user.getId();
  }

  @Override
  public User getUserFromAuth(Authentication auth) {
    if (auth == null || !auth.isAuthenticated()) {
      return null;
    }
    String username = null;
    String principalClassName = auth.getPrincipal().getClass().getName();
    if (principalClassName.equals("org.springframework.security.core.userdetails.User")) {
      username =
          ((org.springframework.security.core.userdetails.User) (auth.getPrincipal()))
              .getUsername();
    } else if (principalClassName.equals("com.novaserve.fitness.users.model.User")) {
      username = ((User) (auth.getPrincipal())).getUsername();
    }
    return userRepository.findByUsername(username).orElse(null);
  }

  @Override
  public String formatCookieExpires(Date date) {
    ZonedDateTime zonedDateTime =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.of("GMT"));
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz");
    return zonedDateTime.format(formatter);
  }
}
