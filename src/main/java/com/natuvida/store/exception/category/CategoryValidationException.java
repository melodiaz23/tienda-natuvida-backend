package com.natuvida.store.exception.category;

public class CategoryValidationException extends RuntimeException {
  private static final String PREFIX = "Error al validar categoría: ";

  public CategoryValidationException(String message) {
    super(PREFIX + message);
  }

  public CategoryValidationException(String message, Throwable cause) {
    super(PREFIX + message, cause);
  }
}