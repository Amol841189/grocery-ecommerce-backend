package com.app.grocery.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // =====================================================
  // PRODUCT ALREADY EXISTS
  // =====================================================

  @ExceptionHandler(ProductAlreadyExistsException.class)
  public ResponseEntity<Map<String, Object>> handleProductAlreadyExists(
    ProductAlreadyExistsException ex
  ) {
    Map<String, Object> response = new LinkedHashMap<>();

    response.put("timestamp", LocalDateTime.now());

    response.put("status", HttpStatus.CONFLICT.value());

    response.put("error", "Product Already Exists");

    response.put("message", ex.getMessage());

    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
  }

  // =====================================================
  // RESOURCE NOT FOUND
  // =====================================================

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleCartNotFound(
    ResourceNotFoundException ex
  ) {
    Map<String, Object> response = new LinkedHashMap<>();

    response.put("timestamp", LocalDateTime.now());

    response.put("status", HttpStatus.NOT_FOUND.value());

    response.put("error", "Resource Not Found");

    response.put("message", ex.getMessage());

    System.out.println(ex.getMessage());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  // =====================================================
  // FOR INSUFFICIENT STOCK
  // =====================================================
  @ExceptionHandler(InsufficientStockException.class)
  public ResponseEntity<String> handleInsufficientStock(
    InsufficientStockException ex
  ) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }
}
