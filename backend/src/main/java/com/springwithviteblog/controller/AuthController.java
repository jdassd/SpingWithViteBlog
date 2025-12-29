package com.springwithviteblog.controller;

import com.springwithviteblog.domain.User;
import com.springwithviteblog.dto.auth.AuthResponse;
import com.springwithviteblog.dto.auth.ChangePasswordRequest;
import com.springwithviteblog.dto.auth.LoginRequest;
import com.springwithviteblog.dto.auth.RefreshRequest;
import com.springwithviteblog.dto.auth.RegisterRequest;
import com.springwithviteblog.dto.common.ApiResponse;
import com.springwithviteblog.exception.ApiException;
import com.springwithviteblog.exception.ErrorCodes;
import com.springwithviteblog.security.SecurityUser;
import com.springwithviteblog.security.SecurityUtils;
import com.springwithviteblog.service.AuthService;
import com.springwithviteblog.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;
  private final UserService userService;

  public AuthController(AuthService authService, UserService userService) {
    this.authService = authService;
    this.userService = userService;
  }

  @PostMapping("/register")
  public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
    User user = authService.register(request.getUsername(), request.getEmail(), request.getPassword());
    AuthResponse response = authService.login(user.getUsername(), request.getPassword());
    return ApiResponse.ok(response);
  }

  @PostMapping("/login")
  public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    return ApiResponse.ok(authService.login(request.getUsername(), request.getPassword()));
  }

  @PostMapping("/refresh")
  public ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
    return ApiResponse.ok(authService.refresh(request.getRefreshToken()));
  }

  @PostMapping("/logout")
  public ApiResponse<Object> logout(@Valid @RequestBody RefreshRequest request) {
    authService.logout(request.getRefreshToken());
    return ApiResponse.ok(null);
  }

  @GetMapping("/me")
  public ApiResponse<AuthResponse.UserSummary> me() {
    SecurityUser securityUser = SecurityUtils.currentUser();
    if (securityUser == null) {
      throw new ApiException(ErrorCodes.UNAUTHORIZED, "Unauthorized", HttpStatus.UNAUTHORIZED);
    }
    AuthResponse.UserSummary summary = new AuthResponse.UserSummary();
    summary.setId(securityUser.getId());
    summary.setUsername(securityUser.getUsername());
    summary.setRole(securityUser.getRole().name());
    return ApiResponse.ok(summary);
  }

  @PostMapping("/change-password")
  public ApiResponse<Object> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
    SecurityUser securityUser = SecurityUtils.currentUser();
    if (securityUser == null) {
      throw new ApiException(ErrorCodes.UNAUTHORIZED, "Unauthorized", HttpStatus.UNAUTHORIZED);
    }
    User user = userService.getById(securityUser.getId());
    if (user == null) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "User not found", HttpStatus.NOT_FOUND);
    }
    userService.changePassword(user, request.getCurrentPassword(), request.getNewPassword());
    return ApiResponse.ok(null);
  }
}
