package com.springwithviteblog.util;

public final class SlugUtils {
  private SlugUtils() {
  }

  public static String toSlug(String input) {
    if (input == null) {
      return "";
    }
    String slug = input.trim().toLowerCase();
    slug = slug.replaceAll("[^a-z0-9\\s-]", "");
    slug = slug.replaceAll("\\s+", "-");
    slug = slug.replaceAll("-+", "-");
    return slug;
  }
}
