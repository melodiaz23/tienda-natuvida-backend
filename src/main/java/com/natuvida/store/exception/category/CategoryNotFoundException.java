package com.natuvida.store.exception.category;

public class CategoryNotFoundException extends RuntimeException {
  private static final String PREFIX = "Categoría no encontrada: ";

  public CategoryNotFoundException(String message) {
    super(PREFIX + message);
  }

  public CategoryNotFoundException(String message, Throwable cause) {
    super(PREFIX + message, cause);
  }
}