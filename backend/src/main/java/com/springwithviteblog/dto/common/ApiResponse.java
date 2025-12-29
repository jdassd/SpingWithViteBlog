package com.springwithviteblog.dto.common;

public class ApiResponse<T> {
  private boolean success;
  private T data;
  private ApiError error;

  public static <T> ApiResponse<T> ok(T data) {
    ApiResponse<T> response = new ApiResponse<>();
    response.success = true;
    response.data = data;
    return response;
  }

  public static <T> ApiResponse<T> error(String code, String message) {
    ApiResponse<T> response = new ApiResponse<>();
    response.success = false;
    response.error = new ApiError(code, message);
    return response;
  }

  public boolean isSuccess() {
    return success;
  }

  public T getData() {
    return data;
  }

  public ApiError getError() {
    return error;
  }
}
