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

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Entidad JPA de la tabla {@code user_goals}.
 */
@Entity
@Table(name = "user_goals")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGoalJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(name = "goal_type", nullable = false)
    private String goalType;

    @Column(name = "target_value")
    private BigDecimal targetValue;

    @Column(name = "target_unit")
    private String targetUnit;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @Column(name = "priority", nullable = false)
    private Short priority;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}