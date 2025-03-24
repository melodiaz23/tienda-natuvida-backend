package com.natuvida.store.api.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ApiResponse<T> {
  private boolean success;
  private String message;
  private T data;
  private List<String> errors;

  public ApiResponse(boolean success, String message, T data) {
    this.success = success;
    this.message = message;
    this.data = data;
    this.errors = null;
  }

  public ApiResponse(boolean success, String message, T data, List<String> errors) {
    this.success = success;
    this.message = message;
    this.data = data;
    this.errors = errors;
  }

  public static <T> ApiResponse<T> success(T data, String message) {
    return new ApiResponse<>(true, message, data);
  }

  public static <T> ApiResponse<T> error(String message) {
    return new ApiResponse<>(false, message, null);
  }

  public static <T> ApiResponse<T> error(String message, List<String> errors) {
    return new ApiResponse<>(false, message, null, errors);
  }
}