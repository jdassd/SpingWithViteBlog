package com.springwithviteblog.controller;

import com.springwithviteblog.domain.CustomCodeType;
import com.springwithviteblog.dto.common.ApiResponse;
import com.springwithviteblog.dto.settings.CustomCodeDto;
import com.springwithviteblog.dto.settings.CustomCodeRequest;
import com.springwithviteblog.exception.ApiException;
import com.springwithviteblog.exception.ErrorCodes;
import com.springwithviteblog.security.SecurityUser;
import com.springwithviteblog.security.SecurityUtils;
import com.springwithviteblog.service.AuditService;
import com.springwithviteblog.service.CustomCodeService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/settings/custom-code")
@PreAuthorize("hasRole('ADMIN')")
public class CustomCodeController {
  private final CustomCodeService customCodeService;
  private final AuditService auditService;

  public CustomCodeController(CustomCodeService customCodeService, AuditService auditService) {
    this.customCodeService = customCodeService;
    this.auditService = auditService;
  }

  @PostMapping
  public ApiResponse<CustomCodeDto> create(@Valid @RequestBody CustomCodeRequest request) {
    SecurityUser securityUser = SecurityUtils.currentUser();
    if (securityUser == null) {
      throw new ApiException(ErrorCodes.UNAUTHORIZED, "Unauthorized", HttpStatus.UNAUTHORIZED);
    }
    CustomCodeDto dto = customCodeService.create(request, securityUser.getId());
    auditService.log(securityUser.getId(), "UPDATE_CUSTOM_CODE", request.getType().name(), String.valueOf(dto.getId()), "SUCCESS", null);
    return ApiResponse.ok(dto);
  }

  @GetMapping
  public ApiResponse<List<CustomCodeDto>> list(@RequestParam CustomCodeType type) {
    return ApiResponse.ok(customCodeService.list(type));
  }

  @PostMapping("/{id}/activate")
  public ApiResponse<Object> activate(@PathVariable Long id, @RequestParam CustomCodeType type) {
    customCodeService.activate(id, type);
    return ApiResponse.ok(null);
  }
}
