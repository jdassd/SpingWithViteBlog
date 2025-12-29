package com.springwithviteblog.controller;

import com.springwithviteblog.dto.common.ApiResponse;
import com.springwithviteblog.exception.ApiException;
import com.springwithviteblog.exception.ErrorCodes;
import com.springwithviteblog.service.StorageService;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/assets")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAssetController {
  private final StorageService storageService;

  public AdminAssetController(StorageService storageService) {
    this.storageService = storageService;
  }

  @PostMapping("/logo")
  public ApiResponse<String> uploadLogo(@RequestParam("file") MultipartFile file) {
    return ApiResponse.ok(save("logo", file));
  }

  @PostMapping("/favicon")
  public ApiResponse<String> uploadFavicon(@RequestParam("file") MultipartFile file) {
    return ApiResponse.ok(save("favicon", file));
  }

  private String save(String name, MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new ApiException(ErrorCodes.VALIDATION_ERROR, "File required", HttpStatus.BAD_REQUEST);
    }
    try {
      return storageService.saveSiteAsset(name, file);
    } catch (IOException ex) {
      throw new ApiException("UPLOAD_ERROR", "Failed to upload file", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
