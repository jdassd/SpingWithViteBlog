package com.springwithviteblog.service;

import com.springwithviteblog.domain.AuditLog;
import com.springwithviteblog.mapper.AuditLogMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
  private final AuditLogMapper auditLogMapper;

  public AuditService(AuditLogMapper auditLogMapper) {
    this.auditLogMapper = auditLogMapper;
  }

  public void log(Long userId, String action, String resourceType, String resourceId, String result, String message) {
    AuditLog log = new AuditLog();
    log.setUserId(userId);
    log.setAction(action);
    log.setResourceType(resourceType);
    log.setResourceId(resourceId);
    log.setResult(result);
    log.setMessage(message);
    log.setCreatedAt(LocalDateTime.now());
    auditLogMapper.insert(log);
  }

  public List<AuditLog> list(Long userId, String action, int page, int size) {
    int offset = Math.max(0, (page - 1) * size);
    return auditLogMapper.list(userId, action, offset, size);
  }
}
