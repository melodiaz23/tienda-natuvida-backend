package com.natuvida.store.enums;

public enum PaymentMethod {
  CASH_ON_DELIVERY("ContraEntrega"),
  MOBILE_PAYMENT("Transferencia");

  private final String displayName;

  PaymentMethod(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}