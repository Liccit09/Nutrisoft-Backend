package com.nutrisoft.core.shared.component.common;

import com.nutrisoft.core.shared.ddd.ValueObject;
import lombok.NonNull;

/**
 * ContactInfo Value Object. Represents contact information (phone, value) shared across components.
 *
 * <p>Located in: Shared Kernel - Core Layer This value object can be used by multiple components
 * (Patient, Professional, etc.)
 */
public record Email(@NonNull String value) implements ValueObject {

  public Email {
    if (!value.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
      throw new IllegalArgumentException("Invalid email format");
    }
  }

  public static Email of(@NonNull String value) {
    return new Email(value);
  }
}
