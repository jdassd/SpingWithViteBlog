package com.springwithviteblog.controller;

import com.springwithviteblog.domain.AuditLog;
import com.springwithviteblog.dto.audit.AuditLogDto;
import com.springwithviteblog.dto.common.ApiResponse;
import com.springwithviteblog.service.AuditService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/audit-logs")
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {
  private final AuditService auditService;

  public AuditLogController(AuditService auditService) {
    this.auditService = auditService;
  }

  @GetMapping
  public ApiResponse<List<AuditLogDto>> list(@RequestParam(required = false) Long userId,
                                             @RequestParam(required = false) String action,
                                             @RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "20") int size) {
    List<AuditLogDto> logs = auditService.list(userId, action, page, size)
        .stream()
        .map(this::toDto)
        .collect(Collectors.toList());
    return ApiResponse.ok(logs);
  }

  private AuditLogDto toDto(AuditLog log) {
    AuditLogDto dto = new AuditLogDto();
    dto.setId(log.getId());
    dto.setUserId(log.getUserId());
    dto.setAction(log.getAction());
    dto.setResourceType(log.getResourceType());
    dto.setResourceId(log.getResourceId());
    dto.setResult(log.getResult());
    dto.setMessage(log.getMessage());
    dto.setCreatedAt(log.getCreatedAt());
    return dto;
  }
}
