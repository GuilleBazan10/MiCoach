package com.kineticos.workout.domain;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * Ejecución real de un ejercicio dentro de una sesión (tabla workout_session_exercises).
 */
@Getter
public class SessionExercise {

    private final Long id;
    private final Long sessionId;
    private final Long workoutExerciseId;
    private final Long exerciseId;
    private final Integer setsDone;
    private final BigDecimal weightKg;
    private final Integer reps;
    private final Integer rpe;
    private final Integer durationSeconds;
    private final Integer distanceMeters;
    private final String notes;

    private SessionExercise(Long id, Long sessionId, Long workoutExerciseId, Long exerciseId,
                            Integer setsDone, BigDecimal weightKg, Integer reps, Integer rpe,
                            Integer durationSeconds, Integer distanceMeters, String notes) {
        this.id = id;
        this.sessionId = sessionId;
        this.workoutExerciseId = workoutExerciseId;
        this.exerciseId = exerciseId;
        this.setsDone = setsDone;
        this.weightKg = weightKg;
        this.reps = reps;
        this.rpe = rpe;
        this.durationSeconds = durationSeconds;
        this.distanceMeters = distanceMeters;
        this.notes = notes;
    }

    public static SessionExercise create(Long sessionId, Long workoutExerciseId, Long exerciseId,
                                         Integer setsDone, BigDecimal weightKg, Integer reps,
                                         Integer rpe, Integer durationSeconds,
                                         Integer distanceMeters, String notes) {
        return new SessionExercise(null, sessionId, workoutExerciseId, exerciseId,
                setsDone == null ? 0 : setsDone, weightKg, reps, rpe, durationSeconds,
                distanceMeters, notes);
    }

    public static SessionExercise restore(Long id, Long sessionId, Long workoutExerciseId,
                                          Long exerciseId, Integer setsDone, BigDecimal weightKg,
                                          Integer reps, Integer rpe, Integer durationSeconds,
                                          Integer distanceMeters, String notes) {
        return new SessionExercise(id, sessionId, workoutExerciseId, exerciseId, setsDone,
                weightKg, reps, rpe, durationSeconds, distanceMeters, notes);
    }
}
