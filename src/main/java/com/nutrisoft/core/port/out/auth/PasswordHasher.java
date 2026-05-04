package com.nutrisoft.core.port.out.auth;

/**
 * Port: Password Hasher Service.
 *
 * <p>Located in: Core\Ports\Out\Auth
 *
 * <p>Defines the contract for password hashing operations. Implementations are in the
 * infrastructure layer (e.g., BCryptPasswordHasher).
 *
 * <p>This port is part of the hexagonal architecture that allows the domain to depend on
 * abstractions rather than concrete implementations.
 */
public interface PasswordHasher {

  /**
   * Hash a plaintext password.
   *
   * @param plainPassword The plaintext password to hash
   * @return The hashed password
   */
  String hash(String plainPassword);

  /**
   * Verify a plaintext password against a hash.
   *
   * @param plainPassword The plaintext password to verify
   * @param hash The hashed password to compare against
   * @return true if the plaintext matches the hash, false otherwise
   */
  boolean matches(String plainPassword, String hash);
}

