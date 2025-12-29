package com.springwithviteblog.service;

import com.springwithviteblog.domain.CustomCodeType;
import com.springwithviteblog.domain.CustomCodeVersion;
import com.springwithviteblog.dto.settings.CustomCodeDto;
import com.springwithviteblog.dto.settings.CustomCodeRequest;
import com.springwithviteblog.exception.ApiException;
import com.springwithviteblog.exception.ErrorCodes;
import com.springwithviteblog.mapper.CustomCodeMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class CustomCodeService {
  private final CustomCodeMapper customCodeMapper;
  private final SiteSettingService siteSettingService;

  public CustomCodeService(CustomCodeMapper customCodeMapper, SiteSettingService siteSettingService) {
    this.customCodeMapper = customCodeMapper;
    this.siteSettingService = siteSettingService;
  }

  public CustomCodeDto create(CustomCodeRequest request, Long userId) {
    if (request.getType() == CustomCodeType.JS) {
      boolean jsEnabled = siteSettingService.getBoolean("enable_custom_js", false);
      if (!jsEnabled) {
        throw new ApiException(ErrorCodes.OPERATION_NOT_ALLOWED, "Custom JS disabled", HttpStatus.BAD_REQUEST);
      }
    }
    CustomCodeVersion version = new CustomCodeVersion();
    version.setType(request.getType());
    version.setContent(request.getContent());
    version.setEnabled(request.getEnabled() == null || request.getEnabled());
    version.setCreatedBy(userId);
    version.setCreatedAt(LocalDateTime.now());
    version.setIsActive(true);

    customCodeMapper.deactivateAll(request.getType());
    customCodeMapper.insert(version);
    return toDto(version);
  }

  public List<CustomCodeDto> list(CustomCodeType type) {
    return customCodeMapper.listByType(type).stream().map(this::toDto).collect(Collectors.toList());
  }

  public void activate(Long id, CustomCodeType type) {
    customCodeMapper.deactivateAll(type);
    customCodeMapper.activate(id);
  }

  public String getActiveCss() {
    CustomCodeVersion active = customCodeMapper.findActive(CustomCodeType.CSS);
    return active == null ? null : active.getContent();
  }

  public String getActiveJs() {
    boolean jsEnabled = siteSettingService.getBoolean("enable_custom_js", false);
    if (!jsEnabled) {
      return null;
    }
    CustomCodeVersion active = customCodeMapper.findActive(CustomCodeType.JS);
    if (active == null || !Boolean.TRUE.equals(active.getEnabled())) {
      return null;
    }
    return active.getContent();
  }

  private CustomCodeDto toDto(CustomCodeVersion version) {
    CustomCodeDto dto = new CustomCodeDto();
    dto.setId(version.getId());
    dto.setType(version.getType().name());
    dto.setContent(version.getContent());
    dto.setEnabled(version.getEnabled());
    dto.setIsActive(version.getIsActive());
    dto.setCreatedAt(version.getCreatedAt());
    return dto;
  }
}
