package com.natuvida.store.exception.category;

public class CategoryAlreadyExistsException extends RuntimeException {
  private static final String PREFIX = "Categoría ya existe: ";

  public CategoryAlreadyExistsException(String message) {
    super(PREFIX + message);
  }

  public CategoryAlreadyExistsException(String message, Throwable cause) {
    super(PREFIX + message, cause);
  }
}