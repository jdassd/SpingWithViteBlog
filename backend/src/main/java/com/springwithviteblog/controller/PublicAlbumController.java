package com.springwithviteblog.controller;

import com.springwithviteblog.domain.User;
import com.springwithviteblog.dto.album.AlbumDto;
import com.springwithviteblog.dto.album.PhotoDto;
import com.springwithviteblog.dto.common.ApiResponse;
import com.springwithviteblog.security.SecurityUser;
import com.springwithviteblog.security.SecurityUtils;
import com.springwithviteblog.service.AlbumService;
import com.springwithviteblog.service.UserService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/albums")
public class PublicAlbumController {
  private final AlbumService albumService;
  private final UserService userService;

  public PublicAlbumController(AlbumService albumService, UserService userService) {
    this.albumService = albumService;
    this.userService = userService;
  }

  @GetMapping
  public ApiResponse<List<AlbumDto>> list() {
    return ApiResponse.ok(albumService.listAlbums(currentUser()));
  }

  @GetMapping("/{id}")
  public ApiResponse<AlbumDto> get(@PathVariable Long id) {
    return ApiResponse.ok(albumService.getAlbum(id, currentUser()));
  }

  @GetMapping("/{id}/photos")
  public ApiResponse<List<PhotoDto>> photos(@PathVariable Long id) {
    return ApiResponse.ok(albumService.listPhotos(id, currentUser()));
  }

  private User currentUser() {
    SecurityUser securityUser = SecurityUtils.currentUser();
    if (securityUser == null) {
      return null;
    }
    return userService.getById(securityUser.getId());
  }
}
