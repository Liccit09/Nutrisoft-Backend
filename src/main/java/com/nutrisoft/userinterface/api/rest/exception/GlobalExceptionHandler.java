package com.nutrisoft.userinterface.api.rest.exception;

import com.nutrisoft.userinterface.api.rest.appointment.generated.model.ErrorResponse;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * Global Exception Handler for REST API.
 *
 * <p>Located in: UserInterface\API\Rest\Exception
 *
 * <p>This is a PRIMARY ADAPTER that provides consistent error responses across all REST endpoints.
 * It translates domain exceptions to HTTP responses.
 *
 * <p>Handles business logic exceptions and converts them to appropriate HTTP responses.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /** Handle IllegalArgumentException (Invalid input). Returns 400 Bad Request. */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
      final IllegalArgumentException ex, final WebRequest request) {
    log.warn("Invalid argument: {}", ex.getMessage());

    return ResponseEntity.badRequest()
        .body(buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request));
  }

  /** Handle IllegalStateException (Invalid state transition). Returns 409 Conflict. */
  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponse> handleIllegalStateException(
      final IllegalStateException ex, final WebRequest request) {
    log.warn("Invalid state: {}", ex.getMessage());

    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request));
  }

  /** Handle all other unexpected exceptions. Returns 500 Internal Server Error. */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGlobalException(
      final Exception ex, final WebRequest request) {
    log.error("Unexpected exception", ex);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request));
  }

  /**
   * Build a standardized error response.
   *
   * @param status The HTTP status code
   * @param message The error message
   * @param request The web request
   * @return An ErrorResponse object representing the error
   */
  private ErrorResponse buildErrorResponse(
      final HttpStatus status, final String message, final WebRequest request) {
    return new ErrorResponse()
        .timestamp(OffsetDateTime.now(ZoneId.systemDefault()))
        .status(status.value())
        .error(status.getReasonPhrase())
        .message(message)
        .path(request.getDescription(false).replace("uri=", ""));
  }
}
