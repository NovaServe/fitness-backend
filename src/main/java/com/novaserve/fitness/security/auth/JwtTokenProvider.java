/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.security.auth;

import static java.util.Objects.isNull;

import com.novaserve.fitness.users.model.User;
import com.novaserve.fitness.users.service.UserUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
    @Autowired
    SecurityProps securityProps;

    @Autowired
    UserUtil userUtil;

    public String generateToken(Authentication auth) {
        var user = (User) (auth.getPrincipal());
        var current = new Date(); // In UTC
        var expires = new Date(current.getTime() + securityProps.Jwt().expiresInMilliseconds()); // In UTC
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(current)
                .expiration(expires)
                .signWith(Keys.hmacShaKeyFor(securityProps.Jwt().secret().getBytes()), Jwts.SIG.HS512)
                .compact();
    }

    public String getUsernameFromJwt(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(securityProps.Jwt().secret().getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Date getExpiresFromJwt(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(securityProps.Jwt().secret().getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    public boolean validateToken(String token) {
        var isValid = false;
        try {
            var claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(securityProps.Jwt().secret().getBytes()))
                    .build()
                    .parseSignedClaims(token);
            var signature = claims.getDigest();
            if (isNull(signature) || signature.length == 0) {
                return isValid;
            }

            var issuedAt = claims.getPayload().getIssuedAt();
            var expiredAt = claims.getPayload().getExpiration();
            if ((expiredAt.getTime() - issuedAt.getTime())
                    != securityProps.Jwt().expiresInMilliseconds()) {
                return isValid;
            }

            var username = claims.getPayload().getSubject();
            if (userUtil.getUserByUsernameOrEmailOrPhone(username, username, username)
                    .isEmpty()) {
                return isValid;
            }
        } catch (ExpiredJwtException
                | UnsupportedJwtException
                | MalformedJwtException
                | SignatureException
                | IllegalArgumentException ex) {
            return isValid;
        }
        isValid = true;
        return isValid;
    }
}
