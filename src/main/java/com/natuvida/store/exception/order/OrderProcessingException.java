package com.natuvida.store.exception.order;

public class OrderProcessingException extends Exception {

  public OrderProcessingException(String message) {
    super(message);
  }

  public OrderProcessingException(String message, Throwable cause) {
    super(message, cause);
  }
}