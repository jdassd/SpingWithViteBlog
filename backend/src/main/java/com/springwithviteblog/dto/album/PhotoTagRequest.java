package com.springwithviteblog.dto.album;

import java.util.List;

public class PhotoTagRequest {
  private List<String> tags;

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }
}
