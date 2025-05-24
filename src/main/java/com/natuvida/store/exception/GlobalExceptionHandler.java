package com.natuvida.store.exception;

import com.natuvida.store.api.response.ApiResponse;
import com.natuvida.store.exception.cart.CartItemNotFoundException;
import com.natuvida.store.exception.cart.CartNotFoundException;
import com.natuvida.store.exception.cart.CartUpdateException;
import com.natuvida.store.exception.cart.CartValidationException;
import com.natuvida.store.exception.category.CategoryAlreadyExistsException;
import com.natuvida.store.exception.category.CategoryNotFoundException;
import com.natuvida.store.exception.category.CategoryStatusException;
import com.natuvida.store.exception.category.CategoryValidationException;
import com.natuvida.store.exception.customer.CustomerAlreadyExistsException;
import com.natuvida.store.exception.customer.CustomerNotFoundException;
import com.natuvida.store.exception.customer.CustomerStatusException;
import com.natuvida.store.exception.customer.CustomerValidationException;
import com.natuvida.store.exception.order.OrderNotFoundException;
import com.natuvida.store.exception.order.OrderProcessingException;
import com.natuvida.store.exception.order.OrderStatusException;
import com.natuvida.store.exception.order.OrderValidationException;
import com.natuvida.store.exception.product.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

// intercept exceptions, no try-catch need it
@RestControllerAdvice // For manage the exceptions in a centralized way
public class GlobalExceptionHandler {

  // GLOBALS EXCEPTIONS
  @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(ValidationException ex) {
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(ApiResponse.error("Error de validación: " + ex.getMessage()));
    }

  // AUTHENTICATION EXCEPTIONS
  // Excepciones de Spring Security - User Details
  // Manejo de credenciales inválidas (contraseña incorrecta)
  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ApiResponse<Object>> handleBadCredentials() {
    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(ApiResponse.error("Credenciales inválidas"));
  }

  // Manejo de usuario no encontrado
  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<ApiResponse<Object>> handleUserNotFound() {
    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(ApiResponse.error("Credenciales inválidas"));
  }

  // Usuario deshabilitado (cuenta bloqueada o no activada)
  @ExceptionHandler(DisabledException.class)
  public ResponseEntity<ApiResponse<Object>> handleDisabledUser() {
    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(ApiResponse.error("La cuenta está desactivada. Por favor contacte al administrador"));
  }

  // Cuenta bloqueada (demasiados intentos fallidos)
  @ExceptionHandler(LockedException.class)
  public ResponseEntity<ApiResponse<Object>> handleLockedAccount() {
    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(ApiResponse.error("La cuenta ha sido bloqueada temporalmente. Intente más tarde"));
  }

  // Credenciales expiradas (contraseña necesita cambio)
  @ExceptionHandler(CredentialsExpiredException.class)
  public ResponseEntity<ApiResponse<Object>> handleCredentialsExpired() {
    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(ApiResponse.error("Su contraseña ha expirado. Por favor reestablézcala"));
  }

  // Error en token JWT
  @ExceptionHandler(JwtException.class)
  public ResponseEntity<ApiResponse<Object>> handleJwtException() {
    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(ApiResponse.error("Sesión inválida o expirada. Por favor inicie sesión nuevamente"));
  }

  // Para errores de validación en los DTOs de registro/login
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    List<String> errors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .toList();

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error("Error de validación", errors));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<Object>> handleInvalidUUID(IllegalArgumentException ex) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error("ID inválido: " + ex.getMessage()));
  }

  // PRODUCT EXCEPTIONS
  @ExceptionHandler(ProductException.class)
  public ResponseEntity<ApiResponse<Object>> handleProductNotFound(ProductException ex) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.error(ex.getMessage()));
  }

  @ExceptionHandler(ProductValidationException.class)
  public ResponseEntity<ApiResponse<Object>> handleProductValidation(ProductValidationException ex) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error(ex.getMessage()));
  }

  @ExceptionHandler(ProductPriceException.class)
  public ResponseEntity<ApiResponse<Object>> handleProductPriceError(ProductPriceException ex) {
    return ResponseEntity
        .status(HttpStatus.UNPROCESSABLE_ENTITY)
        .body(ApiResponse.error(ex.getMessage()));
  }

  @ExceptionHandler(ProductNotAvailableException.class)
  public ResponseEntity<ApiResponse<Object>> handleProductNotAvailable(ProductNotAvailableException ex) {
    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(ApiResponse.error(ex.getMessage()));
  }

  @ExceptionHandler(DuplicateProductSlugException.class)
  public ResponseEntity<ApiResponse<Object>> handleDuplicateSlug(DuplicateProductSlugException ex) {
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(ApiResponse.error(ex.getMessage()));
  }

  // CART EXCEPTIONS
  @ExceptionHandler(CartNotFoundException.class)

  public ResponseEntity<ApiResponse<Object>> handleCartNotFound(CartNotFoundException ex) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.error(ex.getMessage()));
  }

  @ExceptionHandler(CartItemNotFoundException.class)
  public ResponseEntity<ApiResponse<Object>> handleCartItemNotFound(CartItemNotFoundException ex) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.error(ex.getMessage()));
  }

  @ExceptionHandler(CartValidationException.class)
  public ResponseEntity<ApiResponse<Object>> handleCartValidation(CartValidationException ex) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error(ex.getMessage()));
  }

  @ExceptionHandler(CartUpdateException.class)
  public ResponseEntity<ApiResponse<Object>> handleCartUpdate(CartUpdateException ex) {
    return ResponseEntity
        .status(HttpStatus.UNPROCESSABLE_ENTITY)
        .body(ApiResponse.error(ex.getMessage()));
  }

  // ORDER EXCEPTIONS
  @ExceptionHandler(OrderNotFoundException.class)
  public ResponseEntity<ApiResponse<Object>> handleOrderNotFound(OrderNotFoundException ex) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.error(ex.getMessage()));
  }

  @ExceptionHandler(OrderValidationException.class)
  public ResponseEntity<ApiResponse<Object>> handleOrderValidation(OrderValidationException ex) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error(ex.getMessage()));
  }

  @ExceptionHandler(OrderStatusException.class)
  public ResponseEntity<ApiResponse<Object>> handleOrderStatus(OrderStatusException ex) {
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(ApiResponse.error(ex.getMessage()));
  }

  @ExceptionHandler(OrderProcessingException.class)
  public ResponseEntity<ApiResponse<Object>> handleOrderProcessing(OrderProcessingException ex) {
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.error(ex.getMessage()));
  }

  // CUSTOMER EXCEPTIONS
  @ExceptionHandler(CustomerNotFoundException.class)
  public ResponseEntity<ApiResponse<Object>> handleCustomerNotFound(CustomerNotFoundException ex) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.error(ex.getMessage()));
  }

  @ExceptionHandler(CustomerValidationException.class)
  public ResponseEntity<ApiResponse<Object>> handleCustomerValidation(CustomerValidationException ex) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error(ex.getMessage()));
  }

  @ExceptionHandler(CustomerAlreadyExistsException.class)
  public ResponseEntity<ApiResponse<Object>> handleCustomerAlreadyExists(CustomerAlreadyExistsException ex) {
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(ApiResponse.error(ex.getMessage()));
  }

  @ExceptionHandler(CustomerStatusException.class)
  public ResponseEntity<ApiResponse<Object>> handleCustomerStatus(CustomerStatusException ex) {
    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(ApiResponse.error(ex.getMessage()));
  }

  // CATEGORY EXCEPTIONS
  @ExceptionHandler(CategoryNotFoundException.class)
  public ResponseEntity<ApiResponse<Object>> handleCategoryNotFound(CategoryNotFoundException ex) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.error(ex.getMessage()));
  }

  @ExceptionHandler(CategoryValidationException.class)
  public ResponseEntity<ApiResponse<Object>> handleCategoryValidation(CategoryValidationException ex) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error(ex.getMessage()));
  }

  @ExceptionHandler(CategoryAlreadyExistsException.class)
  public ResponseEntity<ApiResponse<Object>> handleCategoryAlreadyExists(CategoryAlreadyExistsException ex) {
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(ApiResponse.error(ex.getMessage()));
  }

  @ExceptionHandler(CategoryStatusException.class)
  public ResponseEntity<ApiResponse<Object>> handleCategoryStatus(CategoryStatusException ex) {
    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(ApiResponse.error(ex.getMessage()));
  }

}