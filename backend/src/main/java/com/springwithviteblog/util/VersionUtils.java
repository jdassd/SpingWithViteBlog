package com.springwithviteblog.util;

public final class VersionUtils {
  private VersionUtils() {
  }

  public static boolean isCompatible(String required, String current) {
    if (required == null || required.isBlank()) {
      return true;
    }
    int[] requiredParts = parse(required);
    int[] currentParts = parse(current);
    for (int i = 0; i < Math.max(requiredParts.length, currentParts.length); i++) {
      int r = i < requiredParts.length ? requiredParts[i] : 0;
      int c = i < currentParts.length ? currentParts[i] : 0;
      if (c > r) {
        return true;
      }
      if (c < r) {
        return false;
      }
    }
    return true;
  }

  private static int[] parse(String version) {
    String[] parts = version.replaceAll("[^0-9.]", "").split("\\.");
    int[] nums = new int[parts.length];
    for (int i = 0; i < parts.length; i++) {
      try {
        nums[i] = Integer.parseInt(parts[i]);
      } catch (NumberFormatException ex) {
        nums[i] = 0;
      }
    }
    return nums;
  }
}
