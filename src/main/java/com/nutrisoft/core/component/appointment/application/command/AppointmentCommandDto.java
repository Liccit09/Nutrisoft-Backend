package com.nutrisoft.core.component.appointment.application.command;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

/**
 * DTO for Appointment Command Requests. Used for creating and updating appointments.
 *
 * <p>Located in: Core\Components\Appointment\Application Part of the Application layer of the
 * Appointment component
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentCommandDto {
  private UUID patientId;
  private UUID professionalId;
  private UUID serviceId;
  private LocalDateTime startTime;
  private String mode;
  private String virtualMeetingLink;
}
