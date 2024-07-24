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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
  @Autowired SecurityProps securityProps;

  @Autowired UserUtil userUtil;

  public String generateToken(Authentication auth) {
    User user = (User) (auth.getPrincipal());
    Date curr = new Date(); // In UTC
    Date expire = new Date(curr.getTime() + securityProps.Jwt().expiresInMilliseconds()); // In UTC
    return Jwts.builder()
        .subject(user.getUsername())
        .issuedAt(curr)
        .expiration(expire)
        .signWith(Keys.hmacShaKeyFor(securityProps.Jwt().secret().getBytes()), Jwts.SIG.HS512)
        .compact();
  }

  public String getUsernameFromJwt(String token) {
    Claims claims =
        Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(securityProps.Jwt().secret().getBytes()))
            .build()
            .parseSignedClaims(token)
            .getPayload();
    return claims.getSubject();
  }

  public Date getExpireFromJwt(String token) {
    Claims claims =
        Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(securityProps.Jwt().secret().getBytes()))
            .build()
            .parseSignedClaims(token)
            .getPayload();
    return claims.getExpiration();
  }

  public boolean validateToken(String token) {
    try {
      Jws<Claims> claims =
          Jwts.parser()
              .verifyWith(Keys.hmacShaKeyFor(securityProps.Jwt().secret().getBytes()))
              .build()
              .parseSignedClaims(token);

      byte[] signature = claims.getDigest();
      if (signature == null || signature.length == 0) {
        return false;
      }

      Date issuedAt = claims.getPayload().getIssuedAt();
      Date expiredAt = claims.getPayload().getExpiration();
      if ((expiredAt.getTime() - issuedAt.getTime())
          != securityProps.Jwt().expiresInMilliseconds()) {
        return false;
      }

      String username = claims.getPayload().getSubject();
      if (userUtil.getUserByUsernameOrEmailOrPhone(username, username, username).isEmpty()) {
        return false;
      }

    } catch (ExpiredJwtException
        | UnsupportedJwtException
        | MalformedJwtException
        | SignatureException
        | IllegalArgumentException ex) {
      return false;
    }

    return true;
  }
}
