package com.springwithviteblog.controller;

import com.springwithviteblog.dto.common.ApiResponse;
import com.springwithviteblog.dto.common.TaxonomyDto;
import com.springwithviteblog.mapper.CategoryMapper;
import com.springwithviteblog.mapper.TagMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/taxonomy")
public class PublicTaxonomyController {
  private final CategoryMapper categoryMapper;
  private final TagMapper tagMapper;

  public PublicTaxonomyController(CategoryMapper categoryMapper, TagMapper tagMapper) {
    this.categoryMapper = categoryMapper;
    this.tagMapper = tagMapper;
  }

  @GetMapping("/categories")
  public ApiResponse<List<TaxonomyDto>> categories() {
    return ApiResponse.ok(categoryMapper.listAll().stream().map(category -> {
      TaxonomyDto dto = new TaxonomyDto();
      dto.setId(category.getId());
      dto.setName(category.getName());
      dto.setSlug(category.getSlug());
      return dto;
    }).collect(Collectors.toList()));
  }

  @GetMapping("/tags")
  public ApiResponse<List<TaxonomyDto>> tags() {
    return ApiResponse.ok(tagMapper.listAll().stream().map(tag -> {
      TaxonomyDto dto = new TaxonomyDto();
      dto.setId(tag.getId());
      dto.setName(tag.getName());
      dto.setSlug(tag.getSlug());
      return dto;
    }).collect(Collectors.toList()));
  }
}
