package com.springwithviteblog.dto.auth;

public class AuthResponse {
  private String accessToken;
  private String refreshToken;
  private String tokenType;
  private long expiresInSeconds;
  private UserSummary user;

  public AuthResponse() {
    this.tokenType = "Bearer";
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public String getTokenType() {
    return tokenType;
  }

  public void setTokenType(String tokenType) {
    this.tokenType = tokenType;
  }

  public long getExpiresInSeconds() {
    return expiresInSeconds;
  }

  public void setExpiresInSeconds(long expiresInSeconds) {
    this.expiresInSeconds = expiresInSeconds;
  }

  public UserSummary getUser() {
    return user;
  }

  public void setUser(UserSummary user) {
    this.user = user;
  }

  public static class UserSummary {
    private Long id;
    private String username;
    private String role;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getRole() {
      return role;
    }

    public void setRole(String role) {
      this.role = role;
    }
  }
}
