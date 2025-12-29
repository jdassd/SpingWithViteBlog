package com.springwithviteblog.controller;

import com.springwithviteblog.domain.Role;
import com.springwithviteblog.domain.User;
import com.springwithviteblog.domain.UserStatus;
import com.springwithviteblog.dto.common.ApiResponse;
import com.springwithviteblog.dto.user.CreateUserRequest;
import com.springwithviteblog.dto.user.ResetPasswordRequest;
import com.springwithviteblog.dto.user.UpdateRoleRequest;
import com.springwithviteblog.dto.user.UpdateStatusRequest;
import com.springwithviteblog.dto.user.UserDto;
import com.springwithviteblog.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {
  private final UserService userService;

  public AdminUserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public ApiResponse<List<UserDto>> list(@RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) Role role,
                                         @RequestParam(required = false) UserStatus status) {
    List<UserDto> users = userService.search(keyword, role, status)
        .stream()
        .map(this::toDto)
        .collect(Collectors.toList());
    return ApiResponse.ok(users);
  }

  @PostMapping
  public ApiResponse<UserDto> create(@Valid @RequestBody CreateUserRequest request) {
    User user = userService.createUser(request.getUsername(), request.getEmail(), request.getPassword(), request.getRole(), false);
    return ApiResponse.ok(toDto(user));
  }

  @PatchMapping("/{id}/role")
  public ApiResponse<Object> updateRole(@PathVariable Long id, @Valid @RequestBody UpdateRoleRequest request) {
    userService.updateRole(id, request.getRole());
    return ApiResponse.ok(null);
  }

  @PatchMapping("/{id}/status")
  public ApiResponse<Object> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest request) {
    userService.updateStatus(id, request.getStatus());
    return ApiResponse.ok(null);
  }

  @PostMapping("/{id}/reset-password")
  public ApiResponse<Object> resetPassword(@PathVariable Long id, @Valid @RequestBody ResetPasswordRequest request) {
    userService.resetPassword(id, request.getNewPassword());
    return ApiResponse.ok(null);
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Object> delete(@PathVariable Long id) {
    userService.deleteUser(id);
    return ApiResponse.ok(null);
  }

  private UserDto toDto(User user) {
    UserDto dto = new UserDto();
    dto.setId(user.getId());
    dto.setUsername(user.getUsername());
    dto.setEmail(user.getEmail());
    dto.setRole(user.getRole().name());
    dto.setStatus(user.getStatus().name());
    dto.setIsDefaultAdmin(user.getIsDefaultAdmin());
    dto.setCreatedAt(user.getCreatedAt());
    return dto;
  }
}
