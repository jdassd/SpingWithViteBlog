package com.springwithviteblog.domain;

import lombok.Data;

@Data
public class Tag {
  private Long id;
  private String name;
  private String slug;
}
