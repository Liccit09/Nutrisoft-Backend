package com.nutrisoft.infrastructure.persistence.jpa.schedule.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA Entity for Schedule persistence.
 *
 * <p>Located in: Infrastructure\Persistence\JPA\Schedule\Entity
 *
 * <p>This entity represents the Schedule aggregate root in the database. It maps to the
 * schedules table.
 *
 * <p>NOTE: For this MVP, we're storing the schedule configuration as JSON. In a more mature
 * system, you might normalize this into separate tables.
 */
@Entity
@Table(name = "schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleEntity {

  @Id
  @Column(columnDefinition = "UUID")
  private UUID id;

  @Column(name = "professional_id", nullable = false, columnDefinition = "UUID", unique = true)
  private UUID professionalId;

  @Column(columnDefinition = "TEXT")
  private String weeklyScheduleJson;

  @Column(columnDefinition = "TEXT")
  private String breaksJson;

  @Column(columnDefinition = "TEXT")
  private String specialDaysJson;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;
}
