package com.nutrisoft.core.component.professional.application.listener;

import com.nutrisoft.core.component.professional.application.notification.SendProfessionalWelcomeEmailUseCase;
import com.nutrisoft.core.component.professional.application.usecase.GetProfessionalByIdUseCase;
import com.nutrisoft.core.shared.component.professional.ProfessionalEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.util.UUID;

/**
 * Professional Created Event Listener for Notifications.
 *
 * <p>Located in: Core\Components\Professional\Application\Listener
 *
 * <p>This component listens to ProfessionalCreatedEvent and orchestrates the notification flow by
 * delegating to SendProfessionalWelcomeEmailUseCase.
 *
 * <p>NOTE: Credential registration is handled by ProfessionalRegistrationEventListener in the auth
 * component. This listener is responsible ONLY for orchestrating the welcome email notification.
 *
 * <p>Event Flow: RegisterProfessional -> ProfessionalCreatedEvent -> ProfessionalRegistrationEventListener
 * (Auth) -> RegisterCredential -> CredentialCreatedEvent -> CredentialRegistrationEventListener
 * (Notification) -> SendEmail
 *
 * <p>This listener runs in parallel with the auth flow, providing a welcome email independently.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProfessionalCreatedEventListener {
  private final SendProfessionalWelcomeEmailUseCase sendProfessionalWelcomeEmailUseCase;
  private final GetProfessionalByIdUseCase getProfessionalByIdUseCase;

  /**
   * Handle professional created event and orchestrate welcome email notification.
   *
   * <p>This method is called after the transaction is committed, ensuring the Professional
   * aggregate has been persisted.
   *
   * @param event The ProfessionalCreatedEvent
   */
  @EventListener
  public void handleProfessionalCreatedEvent(
      final ProfessionalEvent.ProfessionalCreatedEvent event) {

    log.info("Handling professional created event for email: {}", event.email());
    try {
      // Extract information from event
      String firstName = event.firstName();
      String lastName = event.lastName();
      String email = event.email();

      // Retrieve professional to get specialization
      UUID professionalId = UUID.fromString(event.aggregateId());
      final var professional = getProfessionalByIdUseCase.execute(professionalId);
      String specialization = professional.getSpecialization();

      // Send welcome email
      sendProfessionalWelcomeEmailUseCase.execute(email, firstName, lastName, specialization);

      log.info("Professional welcome email orchestrated successfully for: {}", email);
    } catch (Exception e) {
      log.error("Error handling professional created event for email: {}", event.email(), e);
      // Don't throw exception - this is an async operation
    }
  }
}


