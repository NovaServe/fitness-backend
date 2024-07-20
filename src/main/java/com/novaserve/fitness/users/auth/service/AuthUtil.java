package com.novaserve.fitness.users.auth.service;

import com.novaserve.fitness.users.model.User;
import java.util.Date;
import org.springframework.security.core.Authentication;

public interface AuthUtil {
    Long getUserIdFromAuthentication(Authentication authentication);

    User getUserFromAuthentication(Authentication authentication);

    String formatCookieExpirationDateTime(Date date);
}
