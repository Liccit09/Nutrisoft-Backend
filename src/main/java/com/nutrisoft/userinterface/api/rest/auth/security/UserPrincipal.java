package com.nutrisoft.userinterface.api.rest.auth.security;

import com.nutrisoft.core.shared.component.auth.domain.Credential;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Custom UserDetails implementation for Spring Security. Created from Credential VO in the new
 * authentication model.
 *
 * <p>Este objeto viaja en el SecurityContext durante toda la sesión autenticada. Por eso incluye
 * aggregateId, que es esencial para relacionar credenciales con agregados.
 *
 * <p>NOTE: Ya no usamos el campo id ya que no dependemos de la entidad User.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPrincipal implements UserDetails {

  private String email; // Email único del usuario (identificador principal)
  private String password; // Password hash
  private Boolean active; // ¿Cuenta activa?
  private String role; // ROLE_PATIENT, ROLE_PROFESSIONAL, ROLE_ADMIN
  private UUID aggregateId; // ID del agregado (Patient, Professional, Admin)

  public static UserPrincipal createFromCredential(Credential credential) {
    return UserPrincipal.builder()
        .email(credential.getEmail().value())
        .password(credential.getPasswordHash())
        .active(credential.isActive())
        .role(credential.getRole().getAuthority())
        .aggregateId(credential.getAggregateId())
        .build();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    String authority = role != null ? role : "ROLE_USER";
    return Collections.singleton(new SimpleGrantedAuthority(authority));
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isEnabled() {
    return active != null && active;
  }
}
