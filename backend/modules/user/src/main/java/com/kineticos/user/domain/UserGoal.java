package com.kineticos.user.domain;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;

/**
 * Objetivo cuantificable con prioridad (tabla user_goals).
 */
@Getter
public class UserGoal {

    private final Long id;
    private final Long profileId;
    private String goalType;
    private BigDecimal targetValue;
    private String targetUnit;
    private LocalDate targetDate;
    private Integer priority;
    private boolean active;
    private final Instant createdAt;
    private Instant updatedAt;

    private UserGoal(Long id, Long profileId, String goalType, BigDecimal targetValue,
                     String targetUnit, LocalDate targetDate, Integer priority, boolean active,
                     Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.profileId = profileId;
        this.goalType = goalType;
        this.targetValue = targetValue;
        this.targetUnit = targetUnit;
        this.targetDate = targetDate;
        this.priority = priority;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UserGoal create(Long profileId, String goalType, BigDecimal targetValue,
                                  String targetUnit, LocalDate targetDate, Integer priority) {
        Instant now = Instant.now();
        return new UserGoal(null, profileId, goalType, targetValue, targetUnit, targetDate,
                priority, true, now, now);
    }

    public static UserGoal restore(Long id, Long profileId, String goalType, BigDecimal targetValue,
                                   String targetUnit, LocalDate targetDate, Integer priority,
                                   boolean active, Instant createdAt, Instant updatedAt) {
        return new UserGoal(id, profileId, goalType, targetValue, targetUnit, targetDate,
                priority, active, createdAt, updatedAt);
    }
}