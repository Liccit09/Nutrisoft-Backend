package com.nutrisoft.infrastructure.auth;

import com.nutrisoft.core.port.out.auth.PasswordHasher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Implementation: BCrypt Password Hasher.
 *
 * <p>Located in: Infrastructure\Auth
 *
 * <p>Implements the PasswordHasher port using Spring Security's BCryptPasswordEncoder.
 * This is an adapter between the domain port and the Spring Security implementation.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BCryptPasswordHasher implements PasswordHasher {

  private final PasswordEncoder passwordEncoder;

  @Override
  public String hash(String plainPassword) {
    log.debug("Hashing password with BCrypt");
    return passwordEncoder.encode(plainPassword);
  }

  @Override
  public boolean matches(String plainPassword, String hash) {
    log.debug("Verifying password with BCrypt");
    return passwordEncoder.matches(plainPassword, hash);
  }
}

