package com.natuvida.store.exception;

import com.natuvida.store.api.response.ApiResponse;
import com.natuvida.store.dto.response.ErrorResponseDTO;
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

import java.time.LocalDateTime;
import java.util.List;

// intercept exceptions, no try-catch need it
@RestControllerAdvice // For manage the exceptions in a centralized way
public class GlobalExceptionHandler {

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

}