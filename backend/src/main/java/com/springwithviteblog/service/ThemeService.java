package com.springwithviteblog.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springwithviteblog.domain.Theme;
import com.springwithviteblog.dto.theme.ThemeDto;
import com.springwithviteblog.dto.theme.ThemeConfigRequest;
import com.springwithviteblog.exception.ApiException;
import com.springwithviteblog.exception.ErrorCodes;
import com.springwithviteblog.mapper.ThemeMapper;
import com.springwithviteblog.util.VersionUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ThemeService {
  private static final String SYSTEM_VERSION = "1.0.0";

  private final ThemeMapper themeMapper;
  private final ObjectMapper objectMapper;
  private final CustomCodeService customCodeService;

  public ThemeService(ThemeMapper themeMapper, ObjectMapper objectMapper, CustomCodeService customCodeService) {
    this.themeMapper = themeMapper;
    this.objectMapper = objectMapper;
    this.customCodeService = customCodeService;
  }

  public ThemeDto importTheme(MultipartFile file, boolean activate) {
    if (file == null || file.isEmpty()) {
      throw new ApiException(ErrorCodes.VALIDATION_ERROR, "Theme zip required", HttpStatus.BAD_REQUEST);
    }
    Path tempDir;
    try {
      tempDir = Files.createTempDirectory("theme-upload");
    } catch (IOException ex) {
      throw new ApiException("THEME_ERROR", "Failed to prepare temp folder", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    try {
      unzip(file.getInputStream(), tempDir);
      Path themeJsonPath = tempDir.resolve("theme.json");
      if (!Files.exists(themeJsonPath)) {
        throw new ApiException(ErrorCodes.VALIDATION_ERROR, "Missing theme.json", HttpStatus.BAD_REQUEST);
      }
      String themeJson = Files.readString(themeJsonPath);
      JsonNode meta = objectMapper.readTree(themeJson);
      String name = getRequired(meta, "name");
      String version = getRequired(meta, "version");
      String author = getOptional(meta, "author");
      String description = getOptional(meta, "description");
      String minSystemVersion = getOptional(meta, "minSystemVersion");
      if (!VersionUtils.isCompatible(minSystemVersion, SYSTEM_VERSION)) {
        throw new ApiException(ErrorCodes.VALIDATION_ERROR, "Theme version not compatible", HttpStatus.BAD_REQUEST);
      }

      String folderName = UUID.randomUUID().toString();
      Path targetDir = themeBasePath().resolve(folderName);
      Files.createDirectories(themeBasePath());
      Files.move(tempDir, targetDir, StandardCopyOption.REPLACE_EXISTING);

      Theme theme = new Theme();
      theme.setName(name);
      theme.setVersion(version);
      theme.setAuthor(author);
      theme.setDescription(description);
      theme.setStoragePath(targetDir.toString());
      theme.setThemeJson(themeJson);
      theme.setIsActive(false);
      theme.setConfigJson(meta.has("config") ? meta.get("config").toString() : null);
      theme.setCreatedAt(LocalDateTime.now());
      theme.setUpdatedAt(LocalDateTime.now());

      themeMapper.insert(theme);
      if (activate) {
        activateTheme(theme.getId());
        theme.setIsActive(true);
      }
      return toDto(theme);
    } catch (ApiException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new ApiException("THEME_ERROR", "Failed to import theme", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  public List<ThemeDto> list() {
    return themeMapper.listAll().stream().map(this::toDto).collect(Collectors.toList());
  }

  public ThemeDto getActive() {
    Theme theme = themeMapper.findActive();
    if (theme == null) {
      return null;
    }
    return toDto(theme);
  }

  public void activateTheme(Long id) {
    Theme theme = themeMapper.findById(id);
    if (theme == null) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "Theme not found", HttpStatus.NOT_FOUND);
    }
    themeMapper.clearActive();
    theme.setIsActive(true);
    theme.setUpdatedAt(LocalDateTime.now());
    themeMapper.update(theme);
  }

  public void updateConfig(Long id, ThemeConfigRequest request) {
    Theme theme = themeMapper.findById(id);
    if (theme == null) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "Theme not found", HttpStatus.NOT_FOUND);
    }
    theme.setConfigJson(request.getConfigJson());
    theme.setUpdatedAt(LocalDateTime.now());
    themeMapper.update(theme);
  }

  public byte[] exportTheme(Long id, boolean includeCustomCode) {
    Theme theme = themeMapper.findById(id);
    if (theme == null) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "Theme not found", HttpStatus.NOT_FOUND);
    }
    Path themeDir = Path.of(theme.getStoragePath());
    if (!Files.exists(themeDir)) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "Theme files missing", HttpStatus.NOT_FOUND);
    }
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
      zipDirectory(themeDir, themeDir, zipOutputStream);
      if (includeCustomCode) {
        String css = customCodeService.getActiveCss();
        String js = customCodeService.getActiveJs();
        if (css != null) {
          zipEntry(zipOutputStream, "custom-css.css", css.getBytes());
        }
        if (js != null) {
          zipEntry(zipOutputStream, "custom-js.js", js.getBytes());
        }
      }
      zipOutputStream.finish();
      return outputStream.toByteArray();
    } catch (IOException ex) {
      throw new ApiException("THEME_ERROR", "Failed to export theme", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  public void delete(Long id) {
    Theme theme = themeMapper.findById(id);
    if (theme == null) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "Theme not found", HttpStatus.NOT_FOUND);
    }
    if (Boolean.TRUE.equals(theme.getIsActive())) {
      throw new ApiException(ErrorCodes.OPERATION_NOT_ALLOWED, "Active theme cannot be deleted", HttpStatus.BAD_REQUEST);
    }
    themeMapper.delete(id);
  }

  private void unzip(InputStream inputStream, Path targetDir) throws IOException {
    try (ZipInputStream zis = new ZipInputStream(inputStream)) {
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        Path resolved = targetDir.resolve(entry.getName()).normalize();
        if (!resolved.startsWith(targetDir)) {
          throw new IOException("Invalid zip entry");
        }
        if (entry.isDirectory()) {
          Files.createDirectories(resolved);
        } else {
          Files.createDirectories(resolved.getParent());
          Files.copy(zis, resolved, StandardCopyOption.REPLACE_EXISTING);
        }
      }
    }
  }

  private void zipDirectory(Path root, Path source, ZipOutputStream zipOutputStream) throws IOException {
    try (var stream = Files.list(source)) {
      stream.forEach(path -> {
        try {
          if (Files.isDirectory(path)) {
            zipDirectory(root, path, zipOutputStream);
          } else {
            String entryName = root.relativize(path).toString().replace("\\", "/");
            zipEntry(zipOutputStream, entryName, Files.readAllBytes(path));
          }
        } catch (IOException ex) {
          throw new RuntimeException(ex);
        }
      });
    }
  }

  private void zipEntry(ZipOutputStream zipOutputStream, String name, byte[] bytes) throws IOException {
    ZipEntry entry = new ZipEntry(name);
    zipOutputStream.putNextEntry(entry);
    zipOutputStream.write(bytes);
    zipOutputStream.closeEntry();
  }

  private String getRequired(JsonNode node, String field) {
    if (!node.hasNonNull(field)) {
      throw new ApiException(ErrorCodes.VALIDATION_ERROR, "Missing " + field, HttpStatus.BAD_REQUEST);
    }
    return node.get(field).asText();
  }

  private String getOptional(JsonNode node, String field) {
    return node.hasNonNull(field) ? node.get(field).asText() : null;
  }

  private Path themeBasePath() {
    return Path.of("data", "themes");
  }

  private ThemeDto toDto(Theme theme) {
    ThemeDto dto = new ThemeDto();
    dto.setId(theme.getId());
    dto.setName(theme.getName());
    dto.setVersion(theme.getVersion());
    dto.setAuthor(theme.getAuthor());
    dto.setDescription(theme.getDescription());
    dto.setIsActive(theme.getIsActive());
    dto.setThemeJson(theme.getThemeJson());
    dto.setConfigJson(theme.getConfigJson());
    dto.setPublicPath(resolvePublicPath(theme.getStoragePath()));
    return dto;
  }

  private String resolvePublicPath(String storagePath) {
    if (storagePath == null || storagePath.isBlank()) {
      return null;
    }
    Path folder = Path.of(storagePath).getFileName();
    if (folder == null) {
      return null;
    }
    return "/themes/" + folder.toString();
  }
}
