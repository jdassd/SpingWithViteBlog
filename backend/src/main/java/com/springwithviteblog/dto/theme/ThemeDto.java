package com.springwithviteblog.dto.theme;

public class ThemeDto {
  private Long id;
  private String name;
  private String version;
  private String author;
  private String description;
  private Boolean isActive;
  private String themeJson;
  private String configJson;
  private String publicPath;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

  public String getThemeJson() {
    return themeJson;
  }

  public void setThemeJson(String themeJson) {
    this.themeJson = themeJson;
  }

  public String getConfigJson() {
    return configJson;
  }

  public void setConfigJson(String configJson) {
    this.configJson = configJson;
  }

  public String getPublicPath() {
    return publicPath;
  }

  public void setPublicPath(String publicPath) {
    this.publicPath = publicPath;
  }
}
