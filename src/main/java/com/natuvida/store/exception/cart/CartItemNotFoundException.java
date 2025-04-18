package com.natuvida.store.exception.cart;

public class CartItemNotFoundException extends RuntimeException {
  public CartItemNotFoundException(String message) {
    super("Item del carrito no encontrado: " + message);
  }
}
