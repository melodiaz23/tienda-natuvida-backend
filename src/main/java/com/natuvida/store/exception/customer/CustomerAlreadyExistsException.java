package com.natuvida.store.exception.customer;

public class CustomerAlreadyExistsException extends RuntimeException {
  private static final String PREFIX = "Cliente ya existe: ";

  public CustomerAlreadyExistsException(String message) {
    super(PREFIX + message);
  }

  public CustomerAlreadyExistsException(String message, Throwable cause) {
    super(PREFIX + message, cause);
  }
}