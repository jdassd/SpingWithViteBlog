package com.springwithviteblog.controller;

import com.springwithviteblog.domain.User;
import com.springwithviteblog.dto.album.AlbumDto;
import com.springwithviteblog.dto.album.AlbumRequest;
import com.springwithviteblog.dto.album.PhotoDto;
import com.springwithviteblog.dto.album.PhotoTagRequest;
import com.springwithviteblog.dto.common.ApiResponse;
import com.springwithviteblog.exception.ApiException;
import com.springwithviteblog.exception.ErrorCodes;
import com.springwithviteblog.security.SecurityUser;
import com.springwithviteblog.security.SecurityUtils;
import com.springwithviteblog.service.AlbumService;
import com.springwithviteblog.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user/albums")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class UserAlbumController {
  private final AlbumService albumService;
  private final UserService userService;

  public UserAlbumController(AlbumService albumService, UserService userService) {
    this.albumService = albumService;
    this.userService = userService;
  }

  @GetMapping
  public ApiResponse<List<AlbumDto>> list() {
    User user = currentUser();
    return ApiResponse.ok(albumService.listByOwner(user.getId()));
  }

  @PostMapping
  public ApiResponse<AlbumDto> create(@Valid @RequestBody AlbumRequest request) {
    return ApiResponse.ok(albumService.createAlbum(currentUser(), request));
  }

  @PutMapping("/{id}")
  public ApiResponse<AlbumDto> update(@PathVariable Long id, @Valid @RequestBody AlbumRequest request) {
    return ApiResponse.ok(albumService.updateAlbum(id, currentUser(), request));
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Object> delete(@PathVariable Long id) {
    albumService.deleteAlbum(id, currentUser());
    return ApiResponse.ok(null);
  }

  @GetMapping("/{id}/photos")
  public ApiResponse<List<PhotoDto>> listPhotos(@PathVariable Long id) {
    return ApiResponse.ok(albumService.listPhotos(id, currentUser()));
  }

  @PostMapping("/{id}/photos")
  public ApiResponse<PhotoDto> upload(@PathVariable Long id,
                                      @RequestPart("file") MultipartFile file,
                                      @RequestParam(defaultValue = "false") boolean autoSync) {
    boolean allowSync = currentUser().getRole().name().equals("ADMIN");
    return ApiResponse.ok(albumService.uploadPhoto(id, currentUser(), file, allowSync && autoSync));
  }

  @DeleteMapping("/photos/{photoId}")
  public ApiResponse<Object> deletePhoto(@PathVariable Long photoId) {
    albumService.deletePhoto(photoId, currentUser());
    return ApiResponse.ok(null);
  }

  @PatchMapping("/photos/{photoId}/tags")
  public ApiResponse<Object> updateTags(@PathVariable Long photoId, @RequestBody PhotoTagRequest request) {
    albumService.updatePhotoTags(photoId, request.getTags());
    return ApiResponse.ok(null);
  }

  @PostMapping("/{id}/cover")
  public ApiResponse<Object> setCover(@PathVariable Long id, @RequestParam Long photoId) {
    albumService.setCover(id, photoId, currentUser());
    return ApiResponse.ok(null);
  }

  private User currentUser() {
    SecurityUser securityUser = SecurityUtils.currentUser();
    if (securityUser == null) {
      throw new ApiException(ErrorCodes.UNAUTHORIZED, "Unauthorized", HttpStatus.UNAUTHORIZED);
    }
    User user = userService.getById(securityUser.getId());
    if (user == null) {
      throw new ApiException(ErrorCodes.UNAUTHORIZED, "Unauthorized", HttpStatus.UNAUTHORIZED);
    }
    return user;
  }
}
