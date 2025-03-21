package com.natuvida.store.exception;

import com.natuvida.store.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

// intercept exceptions, no try-catch need it
@ControllerAdvice // For manage the exceptions in a centralized way
public class GlobalExceptionHandler {

  @ExceptionHandler(ProductNotFoundException.class)
  public ResponseEntity<ErrorResponseDTO> handleProductNotFound(ProductNotFoundException ex) {
    ErrorResponseDTO error = new ErrorResponseDTO("PRODUCT_NOT_FOUND", ex.getMessage(), LocalDateTime.now());
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

}