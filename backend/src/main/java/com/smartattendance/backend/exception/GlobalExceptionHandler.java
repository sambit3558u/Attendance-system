package com.smartattendance.backend.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * ==========================================================
 * Global Exception Handler
 * ----------------------------------------------------------
 * Handles all exceptions across the application
 * and returns clean JSON responses.
 * ==========================================================
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * ======================================================
     * Handle Custom Business Exceptions
     * ======================================================
     */

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(
            ApiException exception
    ) {

        Map<String, Object> response = new HashMap<>();

        response.put("success", false);
        response.put("message", exception.getMessage());
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    /**
     * ======================================================
     * Handle Validation Errors
     * ======================================================
     */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException exception
    ) {

        Map<String, Object> response = new HashMap<>();

        String errorMessage = exception
                .getBindingResult()
                .getFieldError()
                .getDefaultMessage();

        response.put("success", false);
        response.put("message", errorMessage);
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    /**
     * ======================================================
     * Handle IllegalArgumentException
     * ======================================================
     */

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException exception
    ) {

        Map<String, Object> response = new HashMap<>();

        response.put("success", false);
        response.put("message", exception.getMessage());
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    /**
     * ======================================================
     * Handle All Other Exceptions
     * ======================================================
     */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(
            Exception exception
    ) {

        Map<String, Object> response = new HashMap<>();

        response.put("success", false);
        response.put("message", "Something went wrong.");
        response.put("error", exception.getMessage());
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

}