package com.nutrisoft.userinterface.api.rest.patient.exception;

import com.nutrisoft.userinterface.api.rest.patient.generated.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * Exception Handler for Patient API.
 *
 * <p>Located in: UserInterface\API\Rest\Patient\Exception
 *
 * <p>Handles all exceptions thrown by the PatientController and converts them to appropriate HTTP
 * responses. This is a PRIMARY ADAPTER that provides consistent error responses for patient
 * operations.
 *
 * <p>Exception Handling Flow:
 * <ul>
 *   <li>AuthenticationException: Authentication failures → 401
 *   <li>IllegalArgumentException: Validation errors, business logic errors → 400/401
 *   <li>IllegalStateException: Invalid state transitions → 409
 *   <li>Exception: All other unexpected errors → 500
 * </ul>
 */
@Slf4j
@RestControllerAdvice
public class PatientControllerAdvice {

  /**
   * Handle Spring Security AuthenticationException.
   * Returns 401 Unauthorized.
   *
   * @param ex The authentication exception
   * @return 401 Unauthorized with error details
   */
  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorResponse> handleAuthenticationException(
      final AuthenticationException ex, final WebRequest request) {

    log.warn("Authentication exception in Patient API: {}", ex.getMessage());

    ErrorResponse response = new ErrorResponse().error("Authentication failed").message("Invalid credentials");

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
  }

  /**
   * Handle IllegalArgumentException (validation or business logic errors).
   * Returns 400 Bad Request or 401 Unauthorized depending on context.
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
      final IllegalArgumentException ex, final WebRequest request) {

    log.warn("Validation error in Patient API: {}", ex.getMessage());

    ErrorResponse response = new ErrorResponse().error("Validation failed").message(ex.getMessage());
    return ResponseEntity.badRequest().body(response);
  }

  /**
   * Handle IllegalStateException (invalid state transitions).
   * Returns 409 Conflict.
   */
  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponse> handleIllegalStateException(
      final IllegalStateException ex, final WebRequest request) {

    log.warn("Invalid state in Patient API: {}", ex.getMessage());

    ErrorResponse response = new ErrorResponse().error("Invalid state").message(ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
  }

  /**
   * Handle all other unexpected exceptions.
   * Returns 500 Internal Server Error.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGlobalException(
      final Exception ex, final WebRequest request) {

    log.error("Unexpected exception in Patient API", ex);

    ErrorResponse response =
        new ErrorResponse()
            .error("Internal server error")
            .message("An unexpected error occurred");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }
}

