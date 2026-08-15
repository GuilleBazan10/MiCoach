package com.micoach.user.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Entidad JPA de la tabla {@code user_injuries}.
 */
@Entity
@Table(name = "user_injuries")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInjuryJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(name = "body_part", nullable = false)
    private String bodyPart;

    @Column(name = "injury_type", nullable = false)
    private String injuryType;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "notes")
    private String notes;

    @Column(name = "occurred_at")
    private LocalDate occurredAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}