package com.springwithviteblog.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
public class AppProperties {
  private final Security security = new Security();

  public Security getSecurity() {
    return security;
  }

  public static class Security {
    private final Jwt jwt = new Jwt();

    public Jwt getJwt() {
      return jwt;
    }
  }

  public static class Jwt {
    @NotBlank
    private String issuer;

    @Min(1)
    private int accessTokenTtlMinutes;

    @Min(1)
    private int refreshTokenTtlDays;

    @NotBlank
    private String secret;

    public String getIssuer() {
      return issuer;
    }

    public void setIssuer(String issuer) {
      this.issuer = issuer;
    }

    public int getAccessTokenTtlMinutes() {
      return accessTokenTtlMinutes;
    }

    public void setAccessTokenTtlMinutes(int accessTokenTtlMinutes) {
      this.accessTokenTtlMinutes = accessTokenTtlMinutes;
    }

    public int getRefreshTokenTtlDays() {
      return refreshTokenTtlDays;
    }

    public void setRefreshTokenTtlDays(int refreshTokenTtlDays) {
      this.refreshTokenTtlDays = refreshTokenTtlDays;
    }

    public String getSecret() {
      return secret;
    }

    public void setSecret(String secret) {
      this.secret = secret;
    }
  }
}
