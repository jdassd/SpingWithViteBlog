package com.springwithviteblog.domain;

import lombok.Data;

@Data
public class Category {
  private Long id;
  private String name;
  private String slug;
}
