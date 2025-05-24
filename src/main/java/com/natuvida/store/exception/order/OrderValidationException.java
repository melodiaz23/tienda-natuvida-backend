package com.natuvida.store.exception.order;

public class OrderValidationException extends RuntimeException {

  public OrderValidationException(String message) {
    super(message);
  }

  public OrderValidationException(String message, Throwable cause) {
    super(message, cause);
  }
}