package com.nutrisoft.infrastructure.persistence.jpa.auth.mapper;

import com.nutrisoft.core.shared.component.auth.domain.Credential;
import com.nutrisoft.infrastructure.persistence.jpa.auth.entity.CredentialEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting Credential VO to JPA Entity and vice versa.
 *
 * <p>Located in: Infrastructure\Persistence\JPA\Auth\Mapper
 */
@Mapper(componentModel = "spring")
public interface CredentialMapper {

  @Mapping(target = "email", source = "email.value")
  CredentialEntity toEntity(Credential credential);

  @Mapping(target = "email", expression = "java(Email.of(entity.getEmail()))")
  Credential toDomain(CredentialEntity entity);
}
