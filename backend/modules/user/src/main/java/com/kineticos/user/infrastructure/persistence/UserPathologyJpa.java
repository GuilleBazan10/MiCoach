package com.kineticos.user.infrastructure.persistence;

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
 * Entidad JPA de la tabla {@code user_pathologies}.
 */
@Entity
@Table(name = "user_pathologies")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPathologyJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(name = "pathology", nullable = false)
    private String pathology;

    @Column(name = "notes")
    private String notes;

    @Column(name = "diagnosed_at")
    private LocalDate diagnosedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}