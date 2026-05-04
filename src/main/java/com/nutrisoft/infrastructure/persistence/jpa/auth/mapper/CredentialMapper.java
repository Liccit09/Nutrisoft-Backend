package com.nutrisoft.infrastructure.persistence.jpa.auth.mapper;

import com.nutrisoft.core.shared.component.auth.domain.Credential;
import com.nutrisoft.core.shared.component.auth.domain.UserRole;
import com.nutrisoft.core.shared.component.common.Email;
import com.nutrisoft.core.shared.mapper.ValueObjectMapper;
import com.nutrisoft.infrastructure.persistence.jpa.auth.entity.CredentialEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting Credential Aggregate Root to JPA Entity and vice versa.
 *
 * <p>Located in: Infrastructure\Persistence\JPA\Auth\Mapper
 *
 * <p>IMPORTANT: This mapper explicitly maps ALL fields including: - id: CredentialId → UUID -
 * aggregateId: UUID (Patient/Professional ID) - CRITICAL FIELD - email: Email VO → String -
 * passwordHash: String - role: UserRole enum → String - createdAt, updatedAt: LocalDateTime -
 * active: boolean
 *
 * <p>Bug Fix: Uses helper methods to ensure proper conversion of complex types instead of complex
 * expressions which may not compile correctly in MapStruct.
 */
@Mapper(componentModel = "spring", uses = ValueObjectMapper.class)
public interface CredentialMapper {

  @Mapping(source = "email.value", target = "email")
  @Mapping(source = "role.authority", target = "role")
  CredentialEntity toEntity(Credential credential);

  Credential toDomain(CredentialEntity entity);

  default Email mapEmail(String email) {
    return email != null ? Email.of(email) : null;
  }

  default UserRole mapRole(String role) {
    return role != null ? UserRole.fromString(role.replace("ROLE_", "")) : null;
  }
}
