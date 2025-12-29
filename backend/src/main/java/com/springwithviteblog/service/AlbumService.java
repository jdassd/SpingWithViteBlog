package com.springwithviteblog.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.springwithviteblog.domain.Album;
import com.springwithviteblog.domain.Photo;
import com.springwithviteblog.domain.Role;
import com.springwithviteblog.domain.SyncStatus;
import com.springwithviteblog.domain.User;
import com.springwithviteblog.domain.Visibility;
import com.springwithviteblog.dto.album.AlbumDto;
import com.springwithviteblog.dto.album.AlbumRequest;
import com.springwithviteblog.dto.album.PhotoDto;
import com.springwithviteblog.exception.ApiException;
import com.springwithviteblog.exception.ErrorCodes;
import com.springwithviteblog.mapper.AlbumMapper;
import com.springwithviteblog.mapper.AlbumWhitelistMapper;
import com.springwithviteblog.mapper.PhotoMapper;
import com.springwithviteblog.mapper.PhotoTagMapper;
import com.springwithviteblog.util.SlugUtils;
import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AlbumService {
  private final AlbumMapper albumMapper;
  private final PhotoMapper photoMapper;
  private final PhotoTagMapper photoTagMapper;
  private final AlbumWhitelistMapper albumWhitelistMapper;
  private final StorageService storageService;
  private final PhotoSyncService photoSyncService;
  private final SiteSettingService siteSettingService;
  private final ObjectMapper objectMapper;

  public AlbumService(AlbumMapper albumMapper,
                      PhotoMapper photoMapper,
                      PhotoTagMapper photoTagMapper,
                      AlbumWhitelistMapper albumWhitelistMapper,
                      StorageService storageService,
                      PhotoSyncService photoSyncService,
                      SiteSettingService siteSettingService,
                      ObjectMapper objectMapper) {
    this.albumMapper = albumMapper;
    this.photoMapper = photoMapper;
    this.photoTagMapper = photoTagMapper;
    this.albumWhitelistMapper = albumWhitelistMapper;
    this.storageService = storageService;
    this.photoSyncService = photoSyncService;
    this.siteSettingService = siteSettingService;
    this.objectMapper = objectMapper;
  }

  public AlbumDto createAlbum(User owner, AlbumRequest request) {
    if (!isAdmin(owner)) {
      boolean allowUserAlbum = siteSettingService.getBoolean("allow_user_album", false);
      if (!allowUserAlbum) {
        throw new ApiException(ErrorCodes.FORBIDDEN, "Album creation disabled", HttpStatus.FORBIDDEN);
      }
    }
    Album album = new Album();
    album.setTitle(request.getTitle());
    album.setDescription(request.getDescription());
    album.setOwnerId(owner.getId());
    album.setVisibility(request.getVisibility());
    album.setCreatedAt(LocalDateTime.now());
    album.setUpdatedAt(LocalDateTime.now());
    albumMapper.insert(album);
    updateWhitelist(album.getId(), request.getVisibility(), request.getWhitelistUserIds());
    return toDto(album);
  }

  public AlbumDto updateAlbum(Long id, User actor, AlbumRequest request) {
    Album album = albumMapper.findById(id);
    if (album == null) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "Album not found", HttpStatus.NOT_FOUND);
    }
    if (!isAdmin(actor) && !Objects.equals(album.getOwnerId(), actor.getId())) {
      throw new ApiException(ErrorCodes.FORBIDDEN, "Forbidden", HttpStatus.FORBIDDEN);
    }
    album.setTitle(request.getTitle());
    album.setDescription(request.getDescription());
    album.setVisibility(request.getVisibility());
    album.setUpdatedAt(LocalDateTime.now());
    albumMapper.update(album);
    updateWhitelist(album.getId(), request.getVisibility(), request.getWhitelistUserIds());
    return toDto(album);
  }

  public List<AlbumDto> listAlbums(User viewer) {
    List<Album> albums = albumMapper.list(null, null);
    return albums.stream()
        .filter(album -> canView(album, viewer))
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  public List<AlbumDto> listByOwner(Long ownerId) {
    return albumMapper.list(ownerId, null).stream().map(this::toDto).collect(Collectors.toList());
  }

  public AlbumDto getAlbum(Long id, User viewer) {
    Album album = albumMapper.findById(id);
    if (album == null) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "Album not found", HttpStatus.NOT_FOUND);
    }
    if (!canView(album, viewer)) {
      throw new ApiException(ErrorCodes.FORBIDDEN, "Forbidden", HttpStatus.FORBIDDEN);
    }
    return toDto(album);
  }

  public void deleteAlbum(Long id, User actor) {
    Album album = albumMapper.findById(id);
    if (album == null) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "Album not found", HttpStatus.NOT_FOUND);
    }
    if (!isAdmin(actor) && !Objects.equals(album.getOwnerId(), actor.getId())) {
      throw new ApiException(ErrorCodes.FORBIDDEN, "Forbidden", HttpStatus.FORBIDDEN);
    }
    albumMapper.delete(id);
  }

  @Transactional
  public PhotoDto uploadPhoto(Long albumId, User actor, MultipartFile file, boolean autoSync) {
    Album album = albumMapper.findById(albumId);
    if (album == null) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "Album not found", HttpStatus.NOT_FOUND);
    }
    if (!isAdmin(actor) && !Objects.equals(album.getOwnerId(), actor.getId())) {
      throw new ApiException(ErrorCodes.FORBIDDEN, "Forbidden", HttpStatus.FORBIDDEN);
    }

    try {
      String originalPath = storageService.savePhoto(albumId, file);
      String thumbnailPath = storageService.saveThumbnail(albumId, storageService.resolveFile(originalPath));
      Photo photo = new Photo();
      photo.setAlbumId(albumId);
      photo.setFilename(file.getOriginalFilename());
      photo.setOriginalPath(originalPath);
      photo.setThumbnailPath(thumbnailPath);
      photo.setSyncStatus(SyncStatus.LOCAL);
      photo.setCreatedAt(LocalDateTime.now());
      photo.setUpdatedAt(LocalDateTime.now());
      photo.setExifJson(readExif(storageService.resolveFile(originalPath).toFile()));
      photoMapper.insert(photo);

      if (autoSync) {
        photoSyncService.enqueueSync(photo.getId());
      }
      return toDto(photo);
    } catch (Exception ex) {
      throw new ApiException("UPLOAD_ERROR", "Failed to upload photo", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  public List<PhotoDto> listPhotos(Long albumId, User viewer) {
    Album album = albumMapper.findById(albumId);
    if (album == null) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "Album not found", HttpStatus.NOT_FOUND);
    }
    if (!canView(album, viewer)) {
      throw new ApiException(ErrorCodes.FORBIDDEN, "Forbidden", HttpStatus.FORBIDDEN);
    }
    return photoMapper.listByAlbum(albumId).stream().map(this::toDto).collect(Collectors.toList());
  }

  public void deletePhoto(Long photoId, User actor) {
    Photo photo = photoMapper.findById(photoId);
    if (photo == null) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "Photo not found", HttpStatus.NOT_FOUND);
    }
    Album album = albumMapper.findById(photo.getAlbumId());
    if (album == null) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "Album not found", HttpStatus.NOT_FOUND);
    }
    if (!isAdmin(actor) && !Objects.equals(album.getOwnerId(), actor.getId())) {
      throw new ApiException(ErrorCodes.FORBIDDEN, "Forbidden", HttpStatus.FORBIDDEN);
    }
    photoMapper.delete(photoId);
  }

  public void updatePhotoTags(Long photoId, List<String> tags) {
    Photo photo = photoMapper.findById(photoId);
    if (photo == null) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "Photo not found", HttpStatus.NOT_FOUND);
    }
    photoTagMapper.deleteByPhotoId(photoId);
    if (tags == null) {
      return;
    }
    for (String name : tags) {
      if (name == null || name.isBlank()) {
        continue;
      }
      String slug = SlugUtils.toSlug(name);
      com.springwithviteblog.domain.PhotoTag tag = photoTagMapper.findBySlug(slug);
      if (tag == null) {
        tag = new com.springwithviteblog.domain.PhotoTag();
        tag.setName(name.trim());
        tag.setSlug(slug);
        photoTagMapper.insert(tag);
      }
      photoTagMapper.insertRelation(photoId, tag.getId());
    }
  }

  public void setCover(Long albumId, Long photoId, User actor) {
    Album album = albumMapper.findById(albumId);
    if (album == null) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "Album not found", HttpStatus.NOT_FOUND);
    }
    if (!isAdmin(actor) && !Objects.equals(album.getOwnerId(), actor.getId())) {
      throw new ApiException(ErrorCodes.FORBIDDEN, "Forbidden", HttpStatus.FORBIDDEN);
    }
    album.setCoverPhotoId(photoId);
    album.setUpdatedAt(LocalDateTime.now());
    albumMapper.update(album);
  }

  private String readExif(File file) {
    boolean exifEnabled = siteSettingService.getBoolean("exif_enabled", true);
    if (!exifEnabled) {
      return null;
    }
    boolean stripGeo = siteSettingService.getBoolean("strip_exif_geo", true);
    try {
      Metadata metadata = ImageMetadataReader.readMetadata(file);
      Map<String, String> map = new HashMap<>();
      for (Directory directory : metadata.getDirectories()) {
        if (stripGeo && directory.getName().toLowerCase().contains("gps")) {
          continue;
        }
        for (Tag tag : directory.getTags()) {
          map.put(directory.getName() + ":" + tag.getTagName(), tag.getDescription());
        }
      }
      return objectMapper.writeValueAsString(map);
    } catch (Exception ex) {
      return null;
    }
  }

  private boolean canView(Album album, User viewer) {
    if (album.getVisibility() == Visibility.PUBLIC) {
      return true;
    }
    if (viewer == null) {
      return false;
    }
    if (isAdmin(viewer)) {
      boolean adminCanViewPrivate = siteSettingService.getBoolean("admin_can_view_private", true);
      if (album.getVisibility() == Visibility.PRIVATE) {
        return adminCanViewPrivate || Objects.equals(album.getOwnerId(), viewer.getId());
      }
      return true;
    }
    if (album.getVisibility() == Visibility.LOGIN_ONLY) {
      return true;
    }
    if (album.getVisibility() == Visibility.PRIVATE) {
      return Objects.equals(album.getOwnerId(), viewer.getId());
    }
    if (album.getVisibility() == Visibility.WHITELIST) {
      return albumWhitelistMapper.exists(album.getId(), viewer.getId()) > 0;
    }
    return false;
  }

  private void updateWhitelist(Long albumId, Visibility visibility, List<Long> userIds) {
    albumWhitelistMapper.deleteByAlbumId(albumId);
    if (visibility != Visibility.WHITELIST) {
      return;
    }
    if (userIds == null || userIds.isEmpty()) {
      throw new ApiException(ErrorCodes.VALIDATION_ERROR, "Whitelist users required", HttpStatus.BAD_REQUEST);
    }
    for (Long userId : userIds) {
      albumWhitelistMapper.insert(albumId, userId);
    }
  }

  private boolean isAdmin(User user) {
    return user.getRole() == Role.ADMIN;
  }

  private AlbumDto toDto(Album album) {
    AlbumDto dto = new AlbumDto();
    dto.setId(album.getId());
    dto.setTitle(album.getTitle());
    dto.setDescription(album.getDescription());
    dto.setCoverPhotoId(album.getCoverPhotoId());
    dto.setOwnerId(album.getOwnerId());
    dto.setVisibility(album.getVisibility().name());
    dto.setCreatedAt(album.getCreatedAt());
    dto.setUpdatedAt(album.getUpdatedAt());
    dto.setWhitelistUserIds(albumWhitelistMapper.findUserIds(album.getId()));
    return dto;
  }

  private PhotoDto toDto(Photo photo) {
    PhotoDto dto = new PhotoDto();
    dto.setId(photo.getId());
    dto.setAlbumId(photo.getAlbumId());
    dto.setFilename(photo.getFilename());
    dto.setOriginalPath(photo.getOriginalPath());
    dto.setThumbnailPath(photo.getThumbnailPath());
    dto.setExternalUrl(photo.getExternalUrl());
    dto.setSyncStatus(photo.getSyncStatus().name());
    dto.setSyncError(photo.getSyncError());
    dto.setExifJson(photo.getExifJson());
    dto.setTakenAt(photo.getTakenAt());
    dto.setCreatedAt(photo.getCreatedAt());
    dto.setTags(photoTagMapper.findByPhotoId(photo.getId()).stream().map(t -> t.getName()).collect(Collectors.toList()));
    return dto;
  }
}
