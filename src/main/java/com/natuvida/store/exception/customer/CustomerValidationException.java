package com.natuvida.store.exception.customer;

public class CustomerValidationException extends RuntimeException {
  private static final String PREFIX = "Error de validación de cliente: ";

  public CustomerValidationException(String message) {
    super(PREFIX + message);
  }

  public CustomerValidationException(String message, Throwable cause) {
    super(PREFIX + message, cause);
  }
}