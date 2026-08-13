package com.kineticos.progress.domain;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Métrica de seguimiento con histórico (tabla progress_entries): peso, IMC, % grasa,
 * circunferencias, etc.
 */
@Getter
public class ProgressEntry {

    private final Long id;
    private final Long userId;
    private final String metricType;
    private final BigDecimal value;
    private final String unit;
    private final Instant measuredAt;
    private final String notes;
    private final Instant createdAt;

    private ProgressEntry(Long id, Long userId, String metricType, BigDecimal value, String unit,
                          Instant measuredAt, String notes, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.metricType = metricType;
        this.value = value;
        this.unit = unit;
        this.measuredAt = measuredAt;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public static ProgressEntry create(Long userId, String metricType, BigDecimal value, String unit,
                                       Instant measuredAt, String notes) {
        return new ProgressEntry(null, userId, metricType, value, unit,
                measuredAt == null ? Instant.now() : measuredAt, notes, Instant.now());
    }

    public static ProgressEntry restore(Long id, Long userId, String metricType, BigDecimal value, String unit,
                                        Instant measuredAt, String notes, Instant createdAt) {
        return new ProgressEntry(id, userId, metricType, value, unit, measuredAt, notes, createdAt);
    }

    public boolean belongsTo(Long userId) {
        return this.userId.equals(userId);
    }
}
