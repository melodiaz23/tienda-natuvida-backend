package com.natuvida.store.exception.cart;

public class CartNotFoundException extends RuntimeException {
  public CartNotFoundException(String message) {
    super("Carrito no encontrado" + message);
  }
}
