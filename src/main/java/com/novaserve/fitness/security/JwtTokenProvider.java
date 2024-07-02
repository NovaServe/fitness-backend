package com.novaserve.fitness.security;

import com.novaserve.fitness.exception.ApiException;
import com.novaserve.fitness.exception.ErrorMessage;
import com.novaserve.fitness.user.service.UserUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
    @Autowired
    SecurityProps securityProps;

    @Autowired
    UserUtil userUtil;

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expirationDate =
                new Date(currentDate.getTime() + securityProps.Jwt().expirationMilliseconds());

        return Jwts.builder()
                .subject(username)
                .issuedAt(currentDate)
                .expiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(securityProps.Jwt().secret().getBytes()), Jwts.SIG.HS512)
                .compact();
    }

    public String getUsernameFromJwt(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(securityProps.Jwt().secret().getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(securityProps.Jwt().secret().getBytes()))
                    .build()
                    .parseSignedClaims(token);

            byte[] signature = claims.getDigest();
            if (signature == null || signature.length == 0) {
                throw new ApiException(ErrorMessage.INVALID_TOKEN, null, HttpStatus.UNAUTHORIZED);
            }

            Date issuedAt = claims.getPayload().getIssuedAt();
            Date expiredAt = claims.getPayload().getExpiration();
            if ((expiredAt.getTime() - issuedAt.getTime())
                    != securityProps.Jwt().expirationMilliseconds()) {
                throw new ApiException(ErrorMessage.INVALID_TOKEN, null, HttpStatus.UNAUTHORIZED);
            }

            String usernameOrEmail = claims.getPayload().getSubject();
            if (userUtil.getUserByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                    .isEmpty()) {
                throw new ApiException(ErrorMessage.INVALID_TOKEN, null, HttpStatus.UNAUTHORIZED);
            }

        } catch (ExpiredJwtException
                | UnsupportedJwtException
                | MalformedJwtException
                | SignatureException
                | IllegalArgumentException ex) {
            throw new ApiException(ErrorMessage.INVALID_TOKEN, null, HttpStatus.UNAUTHORIZED);
        }

        return true;
    }
}
