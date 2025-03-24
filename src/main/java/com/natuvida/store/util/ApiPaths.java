package com.natuvida.store.util;

public final class ApiPaths {
  public static final String API_BASE = "/api";
  public static final String API_VERSION = "/v1";
  public static final String API_BASE_PATH = API_BASE + API_VERSION;

  // Entity endpoints
  public static final String USERS = API_BASE_PATH + "/users";
  public static final String CUSTOMERS = API_BASE_PATH + "/customers";
  public static final String PRODUCTS = API_BASE_PATH + "/products";
  public static final String CATEGORIES = API_BASE_PATH + "/categories";
  public static final String ORDERS = API_BASE_PATH + "/orders";
  public static final String CART = API_BASE_PATH + "/cart";

  // Auth endpoints
  public static final String AUTH = API_BASE_PATH + "/auth";
  public static final String LOGIN = AUTH + "/login";
  public static final String REGISTER = AUTH + "/register";

  // Private constructor to prevent instantiation
  private ApiPaths() {
    throw new AssertionError("Utility class should not be instantiated");
  }
}