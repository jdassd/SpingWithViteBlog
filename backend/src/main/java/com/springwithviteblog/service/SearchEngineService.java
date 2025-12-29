package com.springwithviteblog.service;

import com.springwithviteblog.domain.SearchEngine;
import com.springwithviteblog.dto.nav.SearchEngineDto;
import com.springwithviteblog.dto.nav.SearchEngineRequest;
import com.springwithviteblog.exception.ApiException;
import com.springwithviteblog.exception.ErrorCodes;
import com.springwithviteblog.mapper.SearchEngineMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class SearchEngineService {
  private final SearchEngineMapper searchEngineMapper;

  public SearchEngineService(SearchEngineMapper searchEngineMapper) {
    this.searchEngineMapper = searchEngineMapper;
  }

  public SearchEngineDto create(SearchEngineRequest request) {
    validate(request.getQueryUrl());
    SearchEngine engine = new SearchEngine();
    engine.setName(request.getName());
    engine.setQueryUrl(request.getQueryUrl());
    engine.setEnabled(request.getEnabled() == null ? true : request.getEnabled());
    engine.setIsDefault(Boolean.TRUE.equals(request.getIsDefault()));
    engine.setCreatedAt(LocalDateTime.now());
    engine.setUpdatedAt(LocalDateTime.now());

    if (engine.getIsDefault()) {
      searchEngineMapper.clearDefault();
    }
    searchEngineMapper.insert(engine);
    return toDto(engine);
  }

  public SearchEngineDto update(Long id, SearchEngineRequest request) {
    validate(request.getQueryUrl());
    SearchEngine existing = searchEngineMapper.findById(id);
    if (existing == null) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "Search engine not found", HttpStatus.NOT_FOUND);
    }
    existing.setName(request.getName());
    existing.setQueryUrl(request.getQueryUrl());
    existing.setEnabled(request.getEnabled() == null ? true : request.getEnabled());
    existing.setIsDefault(Boolean.TRUE.equals(request.getIsDefault()));
    existing.setUpdatedAt(LocalDateTime.now());

    if (existing.getIsDefault()) {
      searchEngineMapper.clearDefault();
    }
    searchEngineMapper.update(existing);
    return toDto(existing);
  }

  public void delete(Long id) {
    int deleted = searchEngineMapper.delete(id);
    if (deleted == 0) {
      throw new ApiException(ErrorCodes.NOT_FOUND, "Search engine not found", HttpStatus.NOT_FOUND);
    }
  }

  public List<SearchEngineDto> listAll() {
    return searchEngineMapper.listAll().stream().map(this::toDto).collect(Collectors.toList());
  }

  public List<SearchEngineDto> listEnabled() {
    return searchEngineMapper.listEnabled().stream().map(this::toDto).collect(Collectors.toList());
  }

  private void validate(String queryUrl) {
    if (queryUrl == null || (!queryUrl.startsWith("http://") && !queryUrl.startsWith("https://"))) {
      throw new ApiException(ErrorCodes.VALIDATION_ERROR, "Invalid URL", HttpStatus.BAD_REQUEST);
    }
    if (!queryUrl.contains("{q}")) {
      throw new ApiException(ErrorCodes.VALIDATION_ERROR, "Query URL must contain {q}", HttpStatus.BAD_REQUEST);
    }
  }

  private SearchEngineDto toDto(SearchEngine engine) {
    SearchEngineDto dto = new SearchEngineDto();
    dto.setId(engine.getId());
    dto.setName(engine.getName());
    dto.setQueryUrl(engine.getQueryUrl());
    dto.setEnabled(engine.getEnabled());
    dto.setIsDefault(engine.getIsDefault());
    return dto;
  }
}
