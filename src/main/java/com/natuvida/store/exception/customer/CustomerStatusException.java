package com.natuvida.store.exception.customer;

public class CustomerStatusException extends RuntimeException {
  private static final String PREFIX = "Error de estado del cliente: ";

  public CustomerStatusException(String message) {
    super(PREFIX + message);
  }

  public CustomerStatusException(String message, Throwable cause) {
    super(PREFIX + message, cause);
  }
}