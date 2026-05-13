package com.nutrisoft.userinterface.api.rest.auth.exception;

import com.nutrisoft.userinterface.api.rest.auth.generated.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for authentication-related errors.
 *
 * <p>Located in: UserInterface\API\REST\Auth\Exception
 *
 * <p>Centralized error handling for consistent error responses across the API.
 */
@Slf4j
@RestControllerAdvice
public class AuthenticationExceptionHandler {

  /**
   * Handle authentication failures (invalid credentials, etc.)
   */
  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
    log.warn("Authentication failed: Invalid credentials");
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ErrorResponse()
            .error("Unauthorized")
            .message("Invalid email or password"));
  }

  /**
   * Handle general authentication exceptions
   */
  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
    log.warn("Authentication error: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ErrorResponse()
            .error("Unauthorized")
            .message("Authentication failed: " + ex.getMessage()));
  }

  /**
   * Handle illegal argument exceptions (account disabled, credential not found, etc.)
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
    log.warn("Illegal argument: {}", ex.getMessage());
    
    String message = ex.getMessage();
    if (message != null && message.contains("disabled")) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(new ErrorResponse()
              .error("Forbidden")
              .message("Account is disabled"));
    }
    
    if (message != null && message.contains("not found")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ErrorResponse()
              .error("Unauthorized")
              .message("Invalid credentials"));
    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse()
            .error("Bad Request")
            .message(message != null ? message : "Invalid request"));
  }

  /**
   * Handle validation errors from request body validation
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex) {
    log.warn("Validation error: {}", ex.getMessage());
    
    String message = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .reduce((e1, e2) -> e1 + ", " + e2)
        .orElse("Validation failed");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse()
            .error("Validation Error")
            .message(message));
  }

  /**
   * Handle generic exceptions (catch-all)
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
    log.error("Unexpected error: ", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse()
            .error("Internal Server Error")
            .message("An unexpected error occurred. Please try again later."));
  }
}

