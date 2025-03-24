package com.natuvida.store.exception;

import com.natuvida.store.api.response.ApiResponse;
import com.natuvida.store.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// intercept exceptions, no try-catch need it
@RestControllerAdvice // For manage the exceptions in a centralized way
public class GlobalExceptionHandler {

  @ExceptionHandler(ProductNotFoundException.class)
  public ResponseEntity<ErrorResponseDTO> handleProductNotFound(ProductNotFoundException ex) {
    ErrorResponseDTO error = new ErrorResponseDTO("PRODUCT_NOT_FOUND", ex.getMessage(), LocalDateTime.now());
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex){
    List<String> errors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .toList();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error("Error de validación", errors));
  }



}