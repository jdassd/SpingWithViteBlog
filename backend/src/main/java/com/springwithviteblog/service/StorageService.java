package com.springwithviteblog.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageService {
  private static final Path BASE_PATH = Path.of("data", "uploads");

  public String savePhoto(Long albumId, MultipartFile file) throws IOException {
    String extension = getExtension(file.getOriginalFilename());
    String fileName = UUID.randomUUID().toString() + extension;
    Path albumPath = BASE_PATH.resolve("albums").resolve(String.valueOf(albumId));
    Files.createDirectories(albumPath);
    Path target = albumPath.resolve(fileName);
    file.transferTo(target);
    return "/uploads/albums/" + albumId + "/" + fileName;
  }

  public String saveSiteAsset(String assetName, MultipartFile file) throws IOException {
    String extension = getExtension(file.getOriginalFilename());
    String fileName = assetName + "-" + UUID.randomUUID() + extension;
    Path assetPath = BASE_PATH.resolve("site");
    Files.createDirectories(assetPath);
    Path target = assetPath.resolve(fileName);
    file.transferTo(target);
    return "/uploads/site/" + fileName;
  }

  public String saveThumbnail(Long albumId, Path originalPath) throws IOException {
    String fileName = UUID.randomUUID().toString() + ".jpg";
    Path albumPath = BASE_PATH.resolve("albums").resolve(String.valueOf(albumId));
    Files.createDirectories(albumPath);
    Path target = albumPath.resolve(fileName);
    net.coobird.thumbnailator.Thumbnails.of(originalPath.toFile())
        .size(320, 320)
        .outputQuality(0.8)
        .outputFormat("jpg")
        .toFile(target.toFile());
    return "/uploads/albums/" + albumId + "/" + fileName;
  }

  public Path resolveFile(String relativePath) {
    String cleaned = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;
    return BASE_PATH.resolve(cleaned.replaceFirst("uploads/", ""));
  }

  private String getExtension(String name) {
    if (name == null) {
      return "";
    }
    int idx = name.lastIndexOf('.');
    if (idx == -1) {
      return "";
    }
    return name.substring(idx);
  }
}
