package com.nutrisoft.core.shared.component.auth.domain;

/**
 * User Roles enumeration. Defines the different roles available in the system.
 *
 * <p>Located in: Shared Kernel - Core Layer
 */
public enum UserRole {
  PATIENT("ROLE_PATIENT"),
  PROFESSIONAL("ROLE_PROFESSIONAL"),
  ADMIN("ROLE_ADMIN");

  private final String authority;

  UserRole(String authority) {
    this.authority = authority;
  }

  public String getAuthority() {
    return authority;
  }

  public static UserRole fromString(String value) {
    try {
      return UserRole.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid role: " + value);
    }
  }
}
