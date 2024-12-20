/*
** Copyright (C) 2024 NovaServe
*/
package com.novaserve.fitness.security.auth;

import com.novaserve.fitness.profiles.model.UserBase;
import com.novaserve.fitness.profiles.service.ProfileUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
    private final SecurityProps securityProps;

    private final ProfileUtil profileUtil;

    public JwtTokenProvider(SecurityProps securityProps, ProfileUtil profileUtil) {
        this.securityProps = securityProps;
        this.profileUtil = profileUtil;
    }

    public String generateToken(UserBase user) {
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
            if (profileUtil
                    .getUserByUsernameOrEmailOrPhone(username, username, username)
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
