package com.kineticos.progress.infrastructure.persistence;

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

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Entidad JPA de la tabla {@code progress_entries}.
 */
@Entity
@Table(name = "progress_entries")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressEntryJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "metric_type", nullable = false)
    private String metricType;

    @Column(name = "value", nullable = false)
    private BigDecimal value;

    @Column(name = "unit", nullable = false)
    private String unit;

    @Column(name = "measured_at", nullable = false)
    private Instant measuredAt;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
