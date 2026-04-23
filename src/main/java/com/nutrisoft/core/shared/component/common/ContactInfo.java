package com.nutrisoft.core.shared.component.common;

import com.nutrisoft.core.shared.ddd.ValueObject;
import lombok.NonNull;

/**
 * ContactInfo Value Object. Represents contact information (phone, value) shared across components.
 *
 * <p>Located in: Shared Kernel - Core Layer This value object can be used by multiple components
 * (Patient, Professional, etc.)
 */
public record ContactInfo(@NonNull String phoneNumber, @NonNull Email email)
    implements ValueObject {

  public static ContactInfo of(@NonNull String phoneNumber, @NonNull Email email) {
    return new ContactInfo(phoneNumber, email);
  }
}
