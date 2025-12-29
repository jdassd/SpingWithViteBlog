package com.springwithviteblog.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springwithviteblog.domain.User;
import com.springwithviteblog.domain.UserStatus;
import com.springwithviteblog.dto.common.ApiResponse;
import com.springwithviteblog.exception.ApiException;
import com.springwithviteblog.exception.ErrorCodes;
import com.springwithviteblog.mapper.UserMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  private final UserMapper userMapper;
  private final ObjectMapper objectMapper;

  public JwtAuthenticationFilter(JwtService jwtService, UserMapper userMapper, ObjectMapper objectMapper) {
    this.jwtService = jwtService;
    this.userMapper = userMapper;
    this.objectMapper = objectMapper;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = authHeader.substring(7);
    try {
      Claims claims = jwtService.parseClaims(token);
      if (!"access".equals(claims.get("type", String.class))) {
        throw new ApiException(ErrorCodes.TOKEN_INVALID, "Invalid token", HttpStatus.UNAUTHORIZED);
      }
      Long userId = Long.parseLong(claims.getSubject());
      User user = userMapper.findById(userId);
      if (user == null) {
        throw new ApiException(ErrorCodes.UNAUTHORIZED, "User not found", HttpStatus.UNAUTHORIZED);
      }
      if (user.getStatus() != UserStatus.ACTIVE) {
        throw new ApiException(ErrorCodes.USER_DISABLED, "User disabled", HttpStatus.FORBIDDEN);
      }

      SecurityUser securityUser = new SecurityUser(user);
      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
          securityUser, null, securityUser.getAuthorities());
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authentication);
    } catch (ApiException ex) {
      SecurityContextHolder.clearContext();
      writeError(response, ex.getStatus().value(), ex.getCode(), ex.getMessage());
      return;
    } catch (JwtException ex) {
      SecurityContextHolder.clearContext();
      writeError(response, HttpStatus.UNAUTHORIZED.value(), ErrorCodes.TOKEN_INVALID, "Invalid token");
      return;
    }

    filterChain.doFilter(request, response);
  }

  private void writeError(HttpServletResponse response, int status, String code, String message) throws IOException {
    response.setStatus(status);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    objectMapper.writeValue(response.getOutputStream(), ApiResponse.error(code, message));
  }
}
