package com.natuvida.store.exception.category;

public class CategoryStatusException extends RuntimeException {
  private static final String PREFIX = "Error de estado de categoría: ";

  public CategoryStatusException(String message) {
    super(PREFIX + message);
  }

  public CategoryStatusException(String message, Throwable cause) {
    super(PREFIX + message, cause);
  }
}