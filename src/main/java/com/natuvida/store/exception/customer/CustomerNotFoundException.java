package com.natuvida.store.exception.customer;

public class CustomerNotFoundException extends RuntimeException {
  private static final String PREFIX = "Cliente no encontrado: ";

  public CustomerNotFoundException(String message) {
    super(PREFIX + message);
  }

  public CustomerNotFoundException(String message, Throwable cause) {
    super(PREFIX + message, cause);
  }
}