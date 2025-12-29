package com.springwithviteblog.service;

import com.springwithviteblog.domain.NavigationGroup;
import com.springwithviteblog.domain.NavigationLink;
import com.springwithviteblog.dto.nav.NavigationGroupDto;
import com.springwithviteblog.dto.nav.NavigationGroupRequest;
import com.springwithviteblog.dto.nav.NavigationLinkDto;
import com.springwithviteblog.dto.nav.NavigationLinkRequest;
import com.springwithviteblog.exception.ApiException;
import com.springwithviteblog.exception.ErrorCodes;
import com.springwithviteblog.mapper.NavigationGroupMapper;
import com.springwithviteblog.mapper.NavigationLinkMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class NavigationService {
  private final NavigationGroupMapper groupMapper;
  private final NavigationLinkMapper linkMapper;

  public NavigationService(NavigationGroupMapper groupMapper, NavigationLinkMapper linkMapper) {
    this.groupMapper = groupMapper;
    this.linkMapper = linkMapper;
  }

  public NavigationGroupDto createGroup(NavigationGroupRequest request) {
    NavigationGroup group = new NavigationGroup();
    group.setName(request.getName());
    group.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
    group.setCreatedAt(LocalDateTime.now());
    group.setUpdatedAt(LocalDateTime.now());
    groupMapper.insert(group);
    return toDto(group, List.of());
  }

  public NavigationGroupDto updateGroup(Long id, NavigationGroupRequest request) {
    NavigationGroup existing = groupMapper.findById(id);
    if (existing == null) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "Group not found", HttpStatus.NOT_FOUND);
    }
    existing.setName(request.getName());
    existing.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
    existing.setUpdatedAt(LocalDateTime.now());
    groupMapper.update(existing);
    List<NavigationLinkDto> links = linkMapper.listByGroup(id).stream().map(this::toDto).collect(Collectors.toList());
    return toDto(existing, links);
  }

  public void deleteGroup(Long id) {
    int deleted = groupMapper.delete(id);
    if (deleted == 0) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "Group not found", HttpStatus.NOT_FOUND);
    }
  }

  public NavigationLinkDto createLink(NavigationLinkRequest request) {
    validateUrl(request.getUrl());
    NavigationLink link = new NavigationLink();
    link.setGroupId(request.getGroupId());
    link.setName(request.getName());
    link.setUrl(request.getUrl());
    link.setIcon(request.getIcon());
    link.setDescription(request.getDescription());
    link.setOpenInNew(Boolean.TRUE.equals(request.getOpenInNew()));
    link.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
    link.setCreatedAt(LocalDateTime.now());
    link.setUpdatedAt(LocalDateTime.now());
    linkMapper.insert(link);
    return toDto(link);
  }

  public NavigationLinkDto updateLink(Long id, NavigationLinkRequest request) {
    validateUrl(request.getUrl());
    NavigationLink existing = linkMapper.findById(id);
    if (existing == null) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "Link not found", HttpStatus.NOT_FOUND);
    }
    existing.setGroupId(request.getGroupId());
    existing.setName(request.getName());
    existing.setUrl(request.getUrl());
    existing.setIcon(request.getIcon());
    existing.setDescription(request.getDescription());
    existing.setOpenInNew(Boolean.TRUE.equals(request.getOpenInNew()));
    existing.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
    existing.setUpdatedAt(LocalDateTime.now());
    linkMapper.update(existing);
    return toDto(existing);
  }

  public void deleteLink(Long id) {
    int deleted = linkMapper.delete(id);
    if (deleted == 0) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "Link not found", HttpStatus.NOT_FOUND);
    }
  }

  public List<NavigationGroupDto> listGroupsWithLinks() {
    List<NavigationGroup> groups = groupMapper.listAll();
    return groups.stream().map(group -> {
      List<NavigationLinkDto> links = linkMapper.listByGroup(group.getId()).stream()
          .map(this::toDto).collect(Collectors.toList());
      return toDto(group, links);
    }).collect(Collectors.toList());
  }

  private void validateUrl(String url) {
    if (url == null || (!url.startsWith("http://") && !url.startsWith("https://"))) {
      throw new ApiException(ErrorCodes.VALIDATION_ERROR, "Invalid URL", HttpStatus.BAD_REQUEST);
    }
  }

  private NavigationGroupDto toDto(NavigationGroup group, List<NavigationLinkDto> links) {
    NavigationGroupDto dto = new NavigationGroupDto();
    dto.setId(group.getId());
    dto.setName(group.getName());
    dto.setSortOrder(group.getSortOrder());
    dto.setLinks(links);
    return dto;
  }

  private NavigationLinkDto toDto(NavigationLink link) {
    NavigationLinkDto dto = new NavigationLinkDto();
    dto.setId(link.getId());
    dto.setGroupId(link.getGroupId());
    dto.setName(link.getName());
    dto.setUrl(link.getUrl());
    dto.setIcon(link.getIcon());
    dto.setDescription(link.getDescription());
    dto.setOpenInNew(link.getOpenInNew());
    dto.setSortOrder(link.getSortOrder());
    return dto;
  }
}
