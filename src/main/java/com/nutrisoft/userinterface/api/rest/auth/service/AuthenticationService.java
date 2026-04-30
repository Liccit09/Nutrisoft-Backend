package com.nutrisoft.userinterface.api.rest.auth.service;

import com.nutrisoft.core.component.patient.application.usecase.RegisterPatientUseCase;
import com.nutrisoft.core.component.professional.application.usecase.RegisterProfessionalUseCase;
import com.nutrisoft.core.shared.component.auth.application.RegisterCredentialUseCase;
import com.nutrisoft.core.shared.component.auth.domain.UserRole;
import com.nutrisoft.userinterface.api.rest.patient.generated.model.RegisterPatientRequest;
import com.nutrisoft.userinterface.api.rest.professional.generated.model.RegisterProfessionalRequest;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authentication Service (Primary Adapter - Delgado).
 *
 * <p>Located in: UserInterface\API\REST\Auth\Service
 *
 * <p>Este es un adaptador primario que actúa como puente entre la capa REST y los casos de uso.
 * Orquesta la creación de agregados y la creación de credenciales delegando a casos de uso.
 *
 * <p>Responsabilidades: - Adaptar DTOs de entrada a parámetros de casos de uso - Invocar casos de
 * uso para crear agregados y credenciales - Coordinación entre ambos
 *
 * <p>Lo que NO hace: - Lógica de negocio de credenciales (responsabilidad de
 * RegisterCredentialUseCase) - Generar JWT tokens (responsabilidad del Controller/Adapter) -
 * Autenticar usuarios (responsabilidad de Spring Security)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService {

  private final RegisterPatientUseCase registerPatientUseCase;
  private final RegisterProfessionalUseCase registerProfessionalUseCase;
  private final RegisterCredentialUseCase registerCredentialUseCase;

  /**
   * Registra un nuevo paciente. Orquesta: Caso de uso Patient + Caso de uso Credential.
   *
   * @param request DTO con datos del paciente
   * @return ID del agregado Patient creado
   */
  public UUID registerPatient(RegisterPatientRequest request) {
    log.info("Orchestrating patient registration for email: {}", request.getEmail());

    final var patientId =
        registerPatientUseCase.execute(
            request.getFirstName(),
            request.getLastName(),
            request.getEmail(),
            request.getPhoneNumber());

    registerCredentialUseCase.execute(
        patientId.value(), request.getEmail(), request.getPassword(), UserRole.PATIENT);

    return patientId.value();
  }

  /**
   * Registra un nuevo profesional. Orquesta: Caso de uso Professional + Caso de uso Credential.
   *
   * @param request DTO con datos del profesional
   * @return ID del agregado Professional creado
   */
  public UUID registerProfessional(RegisterProfessionalRequest request) {
    log.info("Orchestrating professional registration for email: {}", request.getEmail());

    // Validar licenseNumber antes de proceder
    if (request.getLicenseNumber() == null || request.getLicenseNumber().isBlank()) {
      throw new IllegalArgumentException(
          "License number is required for professional registration");
    }

    // 1. Crear el agregado Professional (responsabilidad de caso de uso de dominio)
    final var professionalId =
        registerProfessionalUseCase.execute(
            request.getFirstName(),
            request.getLastName(),
            request.getEmail(),
            request.getPhoneNumber(),
            request.getLicenseNumber());

    // 2. Crear la Credential (responsabilidad de caso de uso compartido)
    registerCredentialUseCase.execute(
        professionalId.value(), request.getEmail(), request.getPassword(), UserRole.PROFESSIONAL);

    return professionalId.value();
  }
}
