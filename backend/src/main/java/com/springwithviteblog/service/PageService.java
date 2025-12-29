package com.springwithviteblog.service;

import com.springwithviteblog.domain.ContentType;
import com.springwithviteblog.domain.Page;
import com.springwithviteblog.domain.Role;
import com.springwithviteblog.domain.User;
import com.springwithviteblog.domain.Visibility;
import com.springwithviteblog.dto.page.PageDto;
import com.springwithviteblog.dto.page.PageRequest;
import com.springwithviteblog.exception.ApiException;
import com.springwithviteblog.exception.ErrorCodes;
import com.springwithviteblog.mapper.PageMapper;
import com.springwithviteblog.mapper.PageWhitelistMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PageService {
  private static final Pattern SLUG_PATTERN = Pattern.compile("^[a-zA-Z0-9/_-]+$");

  private final PageMapper pageMapper;
  private final PageWhitelistMapper pageWhitelistMapper;
  private final ContentProcessor contentProcessor;
  private final SiteSettingService siteSettingService;

  public PageService(PageMapper pageMapper,
                     PageWhitelistMapper pageWhitelistMapper,
                     ContentProcessor contentProcessor,
                     SiteSettingService siteSettingService) {
    this.pageMapper = pageMapper;
    this.pageWhitelistMapper = pageWhitelistMapper;
    this.contentProcessor = contentProcessor;
    this.siteSettingService = siteSettingService;
  }

  public PageDto create(PageRequest request) {
    validateSlug(request.getSlug());
    if (pageMapper.findBySlug(request.getSlug()) != null) {
      throw new ApiException(ErrorCodes.VALIDATION_ERROR, "Slug already exists", HttpStatus.BAD_REQUEST);
    }
    Page page = buildPage(request, new Page());
    page.setCreatedAt(LocalDateTime.now());
    page.setUpdatedAt(LocalDateTime.now());
    pageMapper.insert(page);
    updateWhitelist(page.getId(), request.getVisibility(), request.getWhitelistUserIds());
    return toDto(page, true);
  }

  public PageDto update(Long id, PageRequest request) {
    Page existing = pageMapper.findById(id);
    if (existing == null) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "Page not found", HttpStatus.NOT_FOUND);
    }
    if (!Objects.equals(existing.getSlug(), request.getSlug())) {
      validateSlug(request.getSlug());
      Page slugExists = pageMapper.findBySlug(request.getSlug());
      if (slugExists != null && !slugExists.getId().equals(id)) {
        throw new ApiException(ErrorCodes.VALIDATION_ERROR, "Slug already exists", HttpStatus.BAD_REQUEST);
      }
    }
    Page updated = buildPage(request, existing);
    updated.setUpdatedAt(LocalDateTime.now());
    pageMapper.update(updated);
    updateWhitelist(updated.getId(), request.getVisibility(), request.getWhitelistUserIds());
    return toDto(updated, true);
  }

  public PageDto getBySlug(String slug, User viewer) {
    Page page = pageMapper.findBySlug(slug);
    if (page == null) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "Page not found", HttpStatus.NOT_FOUND);
    }
    if (!canView(page, viewer)) {
      throw new ApiException(ErrorCodes.FORBIDDEN, "Forbidden", HttpStatus.FORBIDDEN);
    }
    return toDto(page, false);
  }

  public List<PageDto> list(String keyword, Visibility visibility, Boolean isNav) {
    return pageMapper.list(keyword, visibility, isNav)
        .stream()
        .map(page -> toDto(page, true))
        .collect(Collectors.toList());
  }

  public PageDto getById(Long id) {
    Page page = pageMapper.findById(id);
    if (page == null) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "Page not found", HttpStatus.NOT_FOUND);
    }
    return toDto(page, true);
  }

  public List<PageDto> listNav(User viewer) {
    return pageMapper.listNav().stream()
        .filter(page -> canView(page, viewer))
        .map(page -> toDto(page, false))
        .collect(Collectors.toList());
  }

  public void delete(Long id) {
    pageWhitelistMapper.deleteByPageId(id);
    int deleted = pageMapper.delete(id);
    if (deleted == 0) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "Page not found", HttpStatus.NOT_FOUND);
    }
  }

  private Page buildPage(PageRequest request, Page page) {
    if (request.getExternalUrl() != null && !request.getExternalUrl().isBlank()) {
      validateExternalUrl(request.getExternalUrl());
      page.setContentType(ContentType.RICH_TEXT);
      page.setContentRaw(null);
      page.setContentHtml(null);
      page.setExternalUrl(request.getExternalUrl());
    } else {
      if (request.getContentType() == null) {
        throw new ApiException(ErrorCodes.VALIDATION_ERROR, "Content type required", HttpStatus.BAD_REQUEST);
      }
      page.setContentType(request.getContentType());
      page.setContentRaw(request.getContentRaw());
      page.setContentHtml(contentProcessor.toSafeHtml(request.getContentType(), request.getContentRaw()));
      page.setExternalUrl(null);
    }
    page.setTitle(request.getTitle());
    page.setSlug(request.getSlug());
    page.setVisibility(request.getVisibility());
    page.setIsNav(Boolean.TRUE.equals(request.getIsNav()));
    page.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
    return page;
  }

  private void validateSlug(String slug) {
    if (slug == null || slug.isBlank() || !SLUG_PATTERN.matcher(slug).matches()) {
      throw new ApiException(ErrorCodes.VALIDATION_ERROR, "Invalid slug", HttpStatus.BAD_REQUEST);
    }
  }

  private void validateExternalUrl(String url) {
    if (!url.startsWith("http://") && !url.startsWith("https://")) {
      throw new ApiException(ErrorCodes.VALIDATION_ERROR, "Invalid URL", HttpStatus.BAD_REQUEST);
    }
  }

  private boolean canView(Page page, User viewer) {
    if (page.getVisibility() == Visibility.PUBLIC) {
      return true;
    }
    if (viewer == null) {
      return false;
    }
    if (viewer.getRole() == Role.ADMIN) {
      boolean adminCanViewPrivate = siteSettingService.getBoolean("admin_can_view_private", true);
      if (page.getVisibility() == Visibility.PRIVATE) {
        return adminCanViewPrivate;
      }
      return true;
    }
    if (page.getVisibility() == Visibility.LOGIN_ONLY) {
      return true;
    }
    if (page.getVisibility() == Visibility.PRIVATE) {
      return false;
    }
    if (page.getVisibility() == Visibility.WHITELIST) {
      return pageWhitelistMapper.exists(page.getId(), viewer.getId()) > 0;
    }
    return false;
  }

  private void updateWhitelist(Long pageId, Visibility visibility, List<Long> userIds) {
    pageWhitelistMapper.deleteByPageId(pageId);
    if (visibility != Visibility.WHITELIST) {
      return;
    }
    if (userIds == null || userIds.isEmpty()) {
      throw new ApiException(ErrorCodes.VALIDATION_ERROR, "Whitelist users required", HttpStatus.BAD_REQUEST);
    }
    for (Long userId : userIds) {
      pageWhitelistMapper.insert(pageId, userId);
    }
  }

  private PageDto toDto(Page page, boolean includeContentRaw) {
    PageDto dto = new PageDto();
    dto.setId(page.getId());
    dto.setTitle(page.getTitle());
    dto.setSlug(page.getSlug());
    dto.setContentType(page.getContentType() == null ? null : page.getContentType().name());
    dto.setContentHtml(page.getContentHtml());
    dto.setContentRaw(includeContentRaw ? page.getContentRaw() : null);
    dto.setVisibility(page.getVisibility().name());
    dto.setIsNav(page.getIsNav());
    dto.setSortOrder(page.getSortOrder());
    dto.setExternalUrl(page.getExternalUrl());
    dto.setCreatedAt(page.getCreatedAt());
    dto.setUpdatedAt(page.getUpdatedAt());
    dto.setWhitelistUserIds(includeContentRaw ? pageWhitelistMapper.findUserIds(page.getId()) : null);
    return dto;
  }
}
