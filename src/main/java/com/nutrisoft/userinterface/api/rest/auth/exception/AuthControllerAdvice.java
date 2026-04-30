package com.nutrisoft.userinterface.api.rest.auth.exception;

import com.nutrisoft.userinterface.api.rest.auth.AuthController;
import com.nutrisoft.userinterface.api.rest.auth.generated.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * Exception Handler for Authentication API.
 *
 * <p>Located in: UserInterface\API\Rest\Auth\Exception
 *
 * <p>Handles all exceptions thrown by the AuthController and converts them to appropriate HTTP
 * responses. This is a PRIMARY ADAPTER that provides consistent error responses for authentication
 * operations.
 */
@Slf4j
@RestControllerAdvice(assignableTypes = AuthController.class)
public class AuthControllerAdvice {

  /**
   * Handle IllegalArgumentException (validation or business logic errors). Returns 400 Bad Request
   * or 401 Unauthorized depending on context.
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
      final IllegalArgumentException ex, final WebRequest request) {

    log.error("Validation error in Auth API: ", ex);

    // Check if it's an authentication error
    if (ex.getMessage() != null
        && (ex.getMessage().contains("Invalid email or password")
            || ex.getMessage().contains("Account is disabled"))) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ErrorResponse().error("Authentication failed").message(ex.getMessage()));
    }

    return ResponseEntity.badRequest()
        .body(new ErrorResponse().error("Validation failed").message(ex.getMessage()));
  }

  /**
   * Handle Spring Security AuthenticationException (bad credentials, account locked, etc).
   * This includes BadCredentialsException (wrong password) and other authentication failures.
   *
   * <p><strong>THIS IS WHERE PASSWORD VALIDATION ERRORS ARE CAUGHT!</strong>
   *
   * <p>When `authenticationManager.authenticate()` fails in the login method with an incorrect
   * password, it throws a BadCredentialsException which is a subclass of AuthenticationException.
   * This handler catches it and converts it to a 401 Unauthorized response with a generic "Invalid
   * email or password" message for security reasons (to prevent username enumeration).
   *
   * @param ex The authentication exception (includes wrong password scenarios)
   * @return 401 Unauthorized with error details
   */
  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorResponse> handleAuthenticationException(
      final AuthenticationException ex, final WebRequest request) {

    log.error("Authentication exception in Auth API: ", ex);

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(
            new ErrorResponse()
                .error("Authentication failed")
                .message("Invalid email or password"));
  }

  /** Handle IllegalStateException (invalid state transitions). Returns 409 Conflict. */
  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponse> handleIllegalStateException(
      final IllegalStateException ex, final WebRequest request) {

    log.error("Invalid state in Auth API: ", ex);

    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ErrorResponse().error("Invalid state").message(ex.getMessage()));
  }

  /** Handle all other unexpected exceptions. Returns 500 Internal Server Error. */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGlobalException(
      final Exception ex, final WebRequest request) {

    log.error("Unexpected exception in Auth API", ex);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            new ErrorResponse()
                .error("Internal server error")
                .message("An unexpected error occurred"));
  }
}
