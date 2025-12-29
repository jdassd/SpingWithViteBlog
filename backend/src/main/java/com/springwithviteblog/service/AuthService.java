package com.springwithviteblog.service;

import com.springwithviteblog.config.AppProperties;
import com.springwithviteblog.domain.RefreshToken;
import com.springwithviteblog.domain.Role;
import com.springwithviteblog.domain.User;
import com.springwithviteblog.domain.UserStatus;
import com.springwithviteblog.dto.auth.AuthResponse;
import com.springwithviteblog.exception.ApiException;
import com.springwithviteblog.exception.ErrorCodes;
import com.springwithviteblog.mapper.RefreshTokenMapper;
import com.springwithviteblog.mapper.UserMapper;
import com.springwithviteblog.security.JwtService;
import com.springwithviteblog.util.TokenHasher;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
  private static final int MAX_FAILED_LOGIN = 5;
  private static final int LOCK_MINUTES = 15;

  private final UserMapper userMapper;
  private final UserService userService;
  private final JwtService jwtService;
  private final RefreshTokenMapper refreshTokenMapper;
  private final PasswordEncoder passwordEncoder;
  private final AppProperties appProperties;

  public AuthService(UserMapper userMapper,
                     UserService userService,
                     JwtService jwtService,
                     RefreshTokenMapper refreshTokenMapper,
                     PasswordEncoder passwordEncoder,
                     AppProperties appProperties) {
    this.userMapper = userMapper;
    this.userService = userService;
    this.jwtService = jwtService;
    this.refreshTokenMapper = refreshTokenMapper;
    this.passwordEncoder = passwordEncoder;
    this.appProperties = appProperties;
  }

  public User register(String username, String email, String password) {
    return userService.createUser(username, email, password, Role.USER, false);
  }

  public AuthResponse login(String username, String password) {
    User user = userMapper.findByUsername(username);
    if (user == null) {
      throw new ApiException(ErrorCodes.INVALID_CREDENTIALS, "Invalid credentials", HttpStatus.UNAUTHORIZED);
    }
    if (user.getStatus() != UserStatus.ACTIVE) {
      throw new ApiException(ErrorCodes.USER_DISABLED, "User disabled", HttpStatus.FORBIDDEN);
    }
    if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
      throw new ApiException(ErrorCodes.USER_LOCKED, "User locked", HttpStatus.FORBIDDEN);
    }
    if (!passwordEncoder.matches(password, user.getPasswordHash())) {
      int nextFailed = user.getFailedLoginCount() == null ? 1 : user.getFailedLoginCount() + 1;
      LocalDateTime lockedUntil = null;
      if (nextFailed >= MAX_FAILED_LOGIN) {
        lockedUntil = LocalDateTime.now().plusMinutes(LOCK_MINUTES);
      }
      userMapper.updateFailedLogin(user.getId(), nextFailed, lockedUntil, LocalDateTime.now());
      throw new ApiException(ErrorCodes.INVALID_CREDENTIALS, "Invalid credentials", HttpStatus.UNAUTHORIZED);
    }

    userMapper.updateLoginSuccess(user.getId(), LocalDateTime.now(), LocalDateTime.now());

    String accessToken = jwtService.generateAccessToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);
    storeRefreshToken(user, refreshToken);

    return buildAuthResponse(user, accessToken, refreshToken);
  }

  public AuthResponse refresh(String refreshToken) {
    Claims claims;
    try {
      claims = jwtService.parseClaims(refreshToken);
    } catch (io.jsonwebtoken.ExpiredJwtException ex) {
      throw new ApiException(ErrorCodes.TOKEN_EXPIRED, "Refresh token expired", HttpStatus.UNAUTHORIZED);
    } catch (JwtException ex) {
      throw new ApiException(ErrorCodes.TOKEN_INVALID, "Invalid refresh token", HttpStatus.UNAUTHORIZED);
    }
    if (!"refresh".equals(claims.get("type", String.class))) {
      throw new ApiException(ErrorCodes.TOKEN_INVALID, "Invalid refresh token", HttpStatus.UNAUTHORIZED);
    }

    String tokenHash = TokenHasher.sha256(refreshToken);
    RefreshToken stored = refreshTokenMapper.findByHash(tokenHash);
    if (stored == null) {
      throw new ApiException(ErrorCodes.TOKEN_INVALID, "Refresh token revoked", HttpStatus.UNAUTHORIZED);
    }
    if (stored.getRevokedAt() != null) {
      throw new ApiException(ErrorCodes.TOKEN_INVALID, "Refresh token revoked", HttpStatus.UNAUTHORIZED);
    }
    if (stored.getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new ApiException(ErrorCodes.TOKEN_EXPIRED, "Refresh token expired", HttpStatus.UNAUTHORIZED);
    }

    User user = userMapper.findById(stored.getUserId());
    if (user == null || user.getStatus() != UserStatus.ACTIVE) {
      throw new ApiException(ErrorCodes.UNAUTHORIZED, "User not available", HttpStatus.UNAUTHORIZED);
    }

    refreshTokenMapper.revoke(stored.getId(), LocalDateTime.now());

    String newAccessToken = jwtService.generateAccessToken(user);
    String newRefreshToken = jwtService.generateRefreshToken(user);
    storeRefreshToken(user, newRefreshToken);

    return buildAuthResponse(user, newAccessToken, newRefreshToken);
  }

  public void logout(String refreshToken) {
    if (refreshToken == null || refreshToken.isBlank()) {
      return;
    }
    String tokenHash = TokenHasher.sha256(refreshToken);
    refreshTokenMapper.revokeByHash(tokenHash, LocalDateTime.now());
  }

  private void storeRefreshToken(User user, String refreshToken) {
    Claims claims = jwtService.parseClaims(refreshToken);
    Date expiration = claims.getExpiration();
    RefreshToken token = new RefreshToken();
    token.setUserId(user.getId());
    token.setTokenHash(TokenHasher.sha256(refreshToken));
    token.setExpiresAt(LocalDateTime.ofInstant(expiration.toInstant(), ZoneId.systemDefault()));
    token.setRevokedAt(null);
    token.setCreatedAt(LocalDateTime.now());
    refreshTokenMapper.insert(token);
  }

  private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
    AuthResponse response = new AuthResponse();
    response.setAccessToken(accessToken);
    response.setRefreshToken(refreshToken);
    response.setExpiresInSeconds(appProperties.getSecurity().getJwt().getAccessTokenTtlMinutes() * 60L);

    AuthResponse.UserSummary summary = new AuthResponse.UserSummary();
    summary.setId(user.getId());
    summary.setUsername(user.getUsername());
    summary.setRole(user.getRole().name());
    response.setUser(summary);
    return response;
  }
}
