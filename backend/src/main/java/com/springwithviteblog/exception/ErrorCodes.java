package com.springwithviteblog.exception;

public final class ErrorCodes {
  public static final String UNAUTHORIZED = "UNAUTHORIZED";
  public static final String FORBIDDEN = "FORBIDDEN";
  public static final String NOT_FOUND = "NOT_FOUND";
  public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
  public static final String USER_EXISTS = "USER_EXISTS";
  public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
  public static final String USER_DISABLED = "USER_DISABLED";
  public static final String USER_LOCKED = "USER_LOCKED";
  public static final String TOKEN_INVALID = "TOKEN_INVALID";
  public static final String TOKEN_EXPIRED = "TOKEN_EXPIRED";
  public static final String OPERATION_NOT_ALLOWED = "OPERATION_NOT_ALLOWED";

  private ErrorCodes() {
  }
}
