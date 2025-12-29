package com.springwithviteblog.controller;

import com.springwithviteblog.dto.common.ApiResponse;
import com.springwithviteblog.dto.theme.ThemeConfigRequest;
import com.springwithviteblog.dto.theme.ThemeDto;
import com.springwithviteblog.security.SecurityUser;
import com.springwithviteblog.security.SecurityUtils;
import com.springwithviteblog.service.AuditService;
import com.springwithviteblog.service.ThemeService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestPart;

@RestController
@RequestMapping("/api/admin/themes")
@PreAuthorize("hasRole('ADMIN')")
public class ThemeController {
  private final ThemeService themeService;
  private final AuditService auditService;

  public ThemeController(ThemeService themeService, AuditService auditService) {
    this.themeService = themeService;
    this.auditService = auditService;
  }

  @GetMapping
  public ApiResponse<List<ThemeDto>> list() {
    return ApiResponse.ok(themeService.list());
  }

  @PostMapping("/import")
  public ApiResponse<ThemeDto> importTheme(@RequestPart("file") MultipartFile file,
                                           @RequestParam(defaultValue = "false") boolean activate) {
    ThemeDto dto = themeService.importTheme(file, activate);
    auditService.log(currentUserId(), "IMPORT_THEME", "Theme", String.valueOf(dto.getId()), "SUCCESS", null);
    return ApiResponse.ok(dto);
  }

  @PostMapping("/{id}/activate")
  public ApiResponse<Object> activate(@PathVariable Long id) {
    themeService.activateTheme(id);
    auditService.log(currentUserId(), "ACTIVATE_THEME", "Theme", String.valueOf(id), "SUCCESS", null);
    return ApiResponse.ok(null);
  }

  @PutMapping("/{id}/config")
  public ApiResponse<Object> updateConfig(@PathVariable Long id, @Valid @RequestBody ThemeConfigRequest request) {
    themeService.updateConfig(id, request);
    return ApiResponse.ok(null);
  }

  @GetMapping("/{id}/export")
  public ResponseEntity<byte[]> exportTheme(@PathVariable Long id,
                                            @RequestParam(defaultValue = "false") boolean includeCustomCode) {
    byte[] zip = themeService.exportTheme(id, includeCustomCode);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=theme-" + id + ".zip")
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(zip);
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Object> delete(@PathVariable Long id) {
    themeService.delete(id);
    auditService.log(currentUserId(), "DELETE_THEME", "Theme", String.valueOf(id), "SUCCESS", null);
    return ApiResponse.ok(null);
  }

  private Long currentUserId() {
    SecurityUser securityUser = SecurityUtils.currentUser();
    return securityUser == null ? null : securityUser.getId();
  }
}
