package com.smartattendance.backend.exception;

/**
 * ==========================================================
 * Custom API Exception
 * ----------------------------------------------------------
 * This exception is thrown whenever
 * a custom business validation fails.
 *
 * Example:
 * - Email already exists
 * - Invalid Password
 * - Invalid Role
 * - User Not Found
 * ==========================================================
 */

public class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(message);
    }

}