package com.springwithviteblog.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springwithviteblog.domain.Photo;
import com.springwithviteblog.domain.SyncStatus;
import com.springwithviteblog.mapper.PhotoMapper;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
public class PhotoSyncService {
  private final PhotoMapper photoMapper;
  private final StorageService storageService;
  private final SiteSettingService siteSettingService;
  private final ObjectMapper objectMapper;
  private final RestTemplate restTemplate = new RestTemplate();

  public PhotoSyncService(PhotoMapper photoMapper, StorageService storageService,
                          SiteSettingService siteSettingService, ObjectMapper objectMapper) {
    this.photoMapper = photoMapper;
    this.storageService = storageService;
    this.siteSettingService = siteSettingService;
    this.objectMapper = objectMapper;
  }

  @Async
  public void enqueueSync(Long photoId) {
    Photo photo = photoMapper.findById(photoId);
    if (photo == null) {
      return;
    }
    photoMapper.updateSyncStatus(photoId, SyncStatus.SYNCING, null, LocalDateTime.now());
    try {
      String provider = siteSettingService.get("git_provider");
      String repo = siteSettingService.get("git_repo");
      String branch = siteSettingService.get("git_branch");
      String token = siteSettingService.get("git_token");
      String prefix = siteSettingService.get("git_path_prefix");
      String message = siteSettingService.get("git_commit_message");
      boolean datePartition = siteSettingService.getBoolean("git_date_partition", true);
      boolean overwrite = siteSettingService.getBoolean("git_overwrite", false);

      if (provider == null || repo == null || token == null) {
        throw new IllegalStateException("Git provider not configured");
      }
      if (branch == null || branch.isBlank()) {
        branch = "main";
      }
      if (prefix == null) {
        prefix = "";
      }
      if (!prefix.endsWith("/") && !prefix.isEmpty()) {
        prefix = prefix + "/";
      }
      if (message == null || message.isBlank()) {
        message = "Upload photo";
      }

      String datePath = "";
      if (datePartition) {
        datePath = DateTimeFormatter.ofPattern("yyyy/MM").format(LocalDateTime.now()) + "/";
      }
      String basePath = prefix + datePath;

      Path originalPath = storageService.resolveFile(photo.getOriginalPath());
      Path thumbPath = storageService.resolveFile(photo.getThumbnailPath());

      String originalRemote = basePath + "photo-" + photo.getId() + getExtension(originalPath);
      String thumbRemote = basePath + "photo-" + photo.getId() + "-thumb.jpg";

      String originalUrl = upload(provider, repo, branch, token, originalRemote, Files.readAllBytes(originalPath), message, overwrite);
      upload(provider, repo, branch, token, thumbRemote, Files.readAllBytes(thumbPath), message, overwrite);

      photo.setExternalUrl(originalUrl);
      photo.setSyncStatus(SyncStatus.SYNCED);
      photo.setSyncError(null);
      photo.setUpdatedAt(LocalDateTime.now());
      photoMapper.update(photo);
    } catch (Exception ex) {
      photoMapper.updateSyncStatus(photoId, SyncStatus.FAILED, ex.getMessage(), LocalDateTime.now());
    }
  }

  public void testConnection() {
    String provider = siteSettingService.get("git_provider");
    String repo = siteSettingService.get("git_repo");
    String token = siteSettingService.get("git_token");
    if (provider == null || repo == null || token == null) {
      throw new IllegalStateException("Git provider not configured");
    }
    if ("GITEE".equalsIgnoreCase(provider)) {
      String url = "https://gitee.com/api/v5/repos/" + repo + "?access_token=" + token;
      restTemplate.getForEntity(url, String.class);
    } else {
      String url = "https://api.github.com/repos/" + repo;
      HttpHeaders headers = new HttpHeaders();
      headers.setBearerAuth(token);
      restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
    }
  }

  private String upload(String provider, String repo, String branch, String token,
                        String path, byte[] content, String message, boolean overwrite) throws Exception {
    String encoded = Base64.getEncoder().encodeToString(content);
    if ("GITEE".equalsIgnoreCase(provider)) {
      return uploadGitee(repo, branch, token, path, encoded, message, overwrite);
    }
    return uploadGithub(repo, branch, token, path, encoded, message, overwrite);
  }

  private String uploadGithub(String repo, String branch, String token, String path, String content, String message, boolean overwrite) throws Exception {
    String url = "https://api.github.com/repos/" + repo + "/contents/" + path;
    Map<String, Object> body = new HashMap<>();
    body.put("message", message);
    body.put("content", content);
    body.put("branch", branch);
    if (overwrite) {
      String sha = getGithubSha(repo, branch, path, token);
      if (sha != null) {
        body.put("sha", sha);
      }
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    headers.setContentType(MediaType.APPLICATION_JSON);
    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(body, headers), String.class);
    JsonNode node = objectMapper.readTree(response.getBody());
    if (node.has("content") && node.get("content").has("download_url")) {
      return node.get("content").get("download_url").asText();
    }
    return "https://raw.githubusercontent.com/" + repo + "/" + branch + "/" + path;
  }

  private String getGithubSha(String repo, String branch, String path, String token) {
    String url = "https://api.github.com/repos/" + repo + "/contents/" + path + "?ref=" + branch;
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    try {
      ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
      JsonNode node = objectMapper.readTree(response.getBody());
      return node.has("sha") ? node.get("sha").asText() : null;
    } catch (Exception ex) {
      return null;
    }
  }

  private String uploadGitee(String repo, String branch, String token, String path, String content, String message, boolean overwrite) throws Exception {
    String url = "https://gitee.com/api/v5/repos/" + repo + "/contents/" + path;
    Map<String, Object> body = new HashMap<>();
    body.put("access_token", token);
    body.put("message", message);
    body.put("content", content);
    body.put("branch", branch);
    if (overwrite) {
      String sha = getGiteeSha(repo, branch, path, token);
      if (sha != null) {
        body.put("sha", sha);
      }
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
    JsonNode node = objectMapper.readTree(response.getBody());
    if (node.has("content") && node.get("content").has("download_url")) {
      return node.get("content").get("download_url").asText();
    }
    return "https://gitee.com/" + repo + "/raw/" + branch + "/" + path;
  }

  private String getGiteeSha(String repo, String branch, String path, String token) {
    String url = "https://gitee.com/api/v5/repos/" + repo + "/contents/" + path + "?access_token=" + token + "&ref=" + branch;
    try {
      ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, String.class);
      JsonNode node = objectMapper.readTree(response.getBody());
      return node.has("sha") ? node.get("sha").asText() : null;
    } catch (Exception ex) {
      return null;
    }
  }

  private String getExtension(Path path) {
    String name = path.getFileName().toString();
    int idx = name.lastIndexOf('.');
    return idx >= 0 ? name.substring(idx) : "";
  }
}
