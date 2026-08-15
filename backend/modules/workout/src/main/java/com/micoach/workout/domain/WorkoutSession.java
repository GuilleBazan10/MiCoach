package com.micoach.workout.domain;

import lombok.Getter;

import java.time.Instant;
import java.util.List;

/**
 * Sesión de entrenamiento realizada, historial (tabla workout_sessions).
 */
@Getter
public class WorkoutSession {

    private final Long id;
    private final Long userId;
    private final Long workoutId;
    private final Long workoutDayId;
    private String status;
    private final Instant startedAt;
    private Instant completedAt;
    private Integer durationSeconds;
    private String notes;
    private List<SessionExercise> exercises;
    private final Instant createdAt;
    private Instant updatedAt;

    private WorkoutSession(Long id, Long userId, Long workoutId, Long workoutDayId, String status,
                           Instant startedAt, Instant completedAt, Integer durationSeconds,
                           String notes, List<SessionExercise> exercises, Instant createdAt,
                           Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.workoutId = workoutId;
        this.workoutDayId = workoutDayId;
        this.status = status;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.durationSeconds = durationSeconds;
        this.notes = notes;
        this.exercises = exercises == null ? List.of() : exercises;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static WorkoutSession start(Long userId, Long workoutId, Long workoutDayId) {
        Instant now = Instant.now();
        return new WorkoutSession(null, userId, workoutId, workoutDayId, "in_progress", now,
                null, null, null, List.of(), now, now);
    }

    public static WorkoutSession restore(Long id, Long userId, Long workoutId, Long workoutDayId,
                                         String status, Instant startedAt, Instant completedAt,
                                         Integer durationSeconds, String notes,
                                         List<SessionExercise> exercises, Instant createdAt,
                                         Instant updatedAt) {
        return new WorkoutSession(id, userId, workoutId, workoutDayId, status, startedAt,
                completedAt, durationSeconds, notes, exercises, createdAt, updatedAt);
    }

    public void complete(Integer durationSeconds, String notes) {
        this.status = "completed";
        this.completedAt = Instant.now();
        this.durationSeconds = durationSeconds;
        this.notes = notes;
        this.updatedAt = Instant.now();
    }

    public void abort(String notes) {
        this.status = "aborted";
        this.completedAt = Instant.now();
        this.notes = notes;
        this.updatedAt = Instant.now();
    }

    public boolean belongsTo(Long userId) {
        return this.userId.equals(userId);
    }
}
