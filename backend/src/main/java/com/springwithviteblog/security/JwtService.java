package com.springwithviteblog.security;

import com.springwithviteblog.config.AppProperties;
import com.springwithviteblog.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtService {
  private final AppProperties appProperties;
  private final SecretKey secretKey;

  public JwtService(AppProperties appProperties) {
    this.appProperties = appProperties;
    byte[] secretBytes = appProperties.getSecurity().getJwt().getSecret().getBytes(StandardCharsets.UTF_8);
    if (secretBytes.length < 32) {
      throw new IllegalStateException("JWT secret must be at least 32 bytes");
    }
    this.secretKey = io.jsonwebtoken.security.Keys.hmacShaKeyFor(secretBytes);
  }

  public String generateAccessToken(User user) {
    Instant now = Instant.now();
    Instant expiresAt = now.plus(appProperties.getSecurity().getJwt().getAccessTokenTtlMinutes(), ChronoUnit.MINUTES);
    return Jwts.builder()
        .subject(String.valueOf(user.getId()))
        .issuer(appProperties.getSecurity().getJwt().getIssuer())
        .issuedAt(Date.from(now))
        .expiration(Date.from(expiresAt))
        .claim("username", user.getUsername())
        .claim("role", user.getRole().name())
        .claim("type", "access")
        .signWith(secretKey)
        .compact();
  }

  public String generateRefreshToken(User user) {
    Instant now = Instant.now();
    Instant expiresAt = now.plus(appProperties.getSecurity().getJwt().getRefreshTokenTtlDays(), ChronoUnit.DAYS);
    return Jwts.builder()
        .subject(String.valueOf(user.getId()))
        .issuer(appProperties.getSecurity().getJwt().getIssuer())
        .issuedAt(Date.from(now))
        .expiration(Date.from(expiresAt))
        .claim("type", "refresh")
        .signWith(secretKey)
        .compact();
  }

  public Claims parseClaims(String token) throws JwtException {
    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  public Long getUserId(String token) {
    Claims claims = parseClaims(token);
    return Long.parseLong(claims.getSubject());
  }
}
