package com.springwithviteblog.service;

import com.springwithviteblog.domain.Role;
import com.springwithviteblog.domain.User;
import com.springwithviteblog.domain.UserStatus;
import com.springwithviteblog.exception.ApiException;
import com.springwithviteblog.exception.ErrorCodes;
import com.springwithviteblog.mapper.UserMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
    this.userMapper = userMapper;
    this.passwordEncoder = passwordEncoder;
  }

  public User createUser(String username, String email, String rawPassword, Role role, boolean isDefaultAdmin) {
    if (userMapper.findByUsername(username) != null) {
      throw new ApiException(ErrorCodes.USER_EXISTS, "Username already exists", HttpStatus.CONFLICT);
    }
    if (email != null && userMapper.findByEmail(email) != null) {
      throw new ApiException(ErrorCodes.USER_EXISTS, "Email already exists", HttpStatus.CONFLICT);
    }
    validatePassword(rawPassword);

    User user = new User();
    user.setUsername(username);
    user.setEmail(email);
    user.setPasswordHash(passwordEncoder.encode(rawPassword));
    user.setRole(role);
    user.setStatus(UserStatus.ACTIVE);
    user.setIsDefaultAdmin(isDefaultAdmin);
    user.setFailedLoginCount(0);
    user.setLockedUntil(null);
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());

    userMapper.insert(user);
    return user;
  }

  public User getByUsername(String username) {
    return userMapper.findByUsername(username);
  }

  public User getById(Long id) {
    return userMapper.findById(id);
  }

  public List<User> search(String keyword, Role role, UserStatus status) {
    return userMapper.search(keyword, role, status);
  }

  public void updateRole(Long userId, Role role) {
    int updated = userMapper.updateRole(userId, role, LocalDateTime.now());
    if (updated == 0) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "User not found", HttpStatus.NOT_FOUND);
    }
  }

  public void updateStatus(Long userId, UserStatus status) {
    int updated = userMapper.updateStatus(userId, status, LocalDateTime.now());
    if (updated == 0) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "User not found", HttpStatus.NOT_FOUND);
    }
  }

  public void resetPassword(Long userId, String newPassword) {
    validatePassword(newPassword);
    int updated = userMapper.updatePassword(userId, passwordEncoder.encode(newPassword), LocalDateTime.now());
    if (updated == 0) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "User not found", HttpStatus.NOT_FOUND);
    }
  }

  public void deleteUser(Long userId) {
    int deleted = userMapper.deleteById(userId);
    if (deleted == 0) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "User not found", HttpStatus.NOT_FOUND);
    }
  }

  public void changePassword(User user, String currentPassword, String newPassword) {
    if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
      throw new ApiException(ErrorCodes.INVALID_CREDENTIALS, "Current password incorrect", HttpStatus.BAD_REQUEST);
    }
    validatePassword(newPassword);
    userMapper.updatePassword(user.getId(), passwordEncoder.encode(newPassword), LocalDateTime.now());
  }

  private void validatePassword(String password) {
    if (password == null || password.length() < 6) {
      throw new ApiException(ErrorCodes.VALIDATION_ERROR, "Password too short", HttpStatus.BAD_REQUEST);
    }
  }
}
