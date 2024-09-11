/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.security.auth;

import com.novaserve.fitness.users.model.User;
import com.novaserve.fitness.users.service.UserUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.util.Date;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
    private final SecurityProps securityProps;

    private final UserUtil userUtil;

    public JwtTokenProvider(SecurityProps securityProps, UserUtil userUtil) {
        this.securityProps = securityProps;
        this.userUtil = userUtil;
    }

    public String generateToken(Authentication auth) {
        User user = (User) (auth.getPrincipal());
        Date current = new Date(); // In UTC
        Date expires = new Date(current.getTime() + securityProps.Jwt().expiresInMilliseconds()); // In UTC
        String token = Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(current)
                .expiration(expires)
                .signWith(Keys.hmacShaKeyFor(securityProps.Jwt().secret().getBytes()), Jwts.SIG.HS512)
                .compact();
        return token;
    }

    public String getUsernameFromJwt(String token) {
        String username = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(securityProps.Jwt().secret().getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        return username;
    }

    public Date getExpiresDateFromJwt(String token) {
        Date expirationDate = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(securityProps.Jwt().secret().getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
        return expirationDate;
    }

    public boolean validateToken(String token) {
        boolean isValid = false;

        try {
            Jws<Claims> claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(securityProps.Jwt().secret().getBytes()))
                    .build()
                    .parseSignedClaims(token);

            byte[] signature = claims.getDigest();
            if (signature == null || signature.length == 0) {
                return isValid;
            }

            Date issuedAt = claims.getPayload().getIssuedAt();
            Date expiredAt = claims.getPayload().getExpiration();
            if ((expiredAt.getTime() - issuedAt.getTime())
                    != securityProps.Jwt().expiresInMilliseconds()) {
                return isValid;
            }

            String username = claims.getPayload().getSubject();
            if (userUtil.getUserByUsernameOrEmailOrPhone(username, username, username)
                    .isEmpty()) {
                return isValid;
            }
        } catch (ExpiredJwtException
                | UnsupportedJwtException
                | MalformedJwtException
                | SignatureException
                | IllegalArgumentException e) {
            return isValid;
        }
        isValid = true;
        return isValid;
    }
}
