package com.micoach.workout.domain;

import lombok.Getter;

/**
 * Prescripción de un ejercicio dentro de un día de rutina (tabla workout_workout_exercises).
 */
@Getter
public class PlannedExercise {

    private final Long id;
    private final Long workoutDayId;
    private final Long exerciseId;
    private final Integer orderIndex;
    private final Integer sets;
    private final Integer repsMin;
    private final Integer repsMax;
    private final Integer restSeconds;
    private final Integer intensityPercent;
    private final String tempo;
    private final String notes;

    private PlannedExercise(Long id, Long workoutDayId, Long exerciseId, Integer orderIndex,
                            Integer sets, Integer repsMin, Integer repsMax, Integer restSeconds,
                            Integer intensityPercent, String tempo, String notes) {
        this.id = id;
        this.workoutDayId = workoutDayId;
        this.exerciseId = exerciseId;
        this.orderIndex = orderIndex;
        this.sets = sets;
        this.repsMin = repsMin;
        this.repsMax = repsMax;
        this.restSeconds = restSeconds;
        this.intensityPercent = intensityPercent;
        this.tempo = tempo;
        this.notes = notes;
    }

    public static PlannedExercise create(Long exerciseId, Integer orderIndex, Integer sets,
                                         Integer repsMin, Integer repsMax, Integer restSeconds,
                                         Integer intensityPercent, String tempo, String notes) {
        return new PlannedExercise(null, null, exerciseId, orderIndex, sets == null ? 1 : sets,
                repsMin, repsMax, restSeconds, intensityPercent, tempo, notes);
    }

    public static PlannedExercise restore(Long id, Long workoutDayId, Long exerciseId,
                                          Integer orderIndex, Integer sets, Integer repsMin,
                                          Integer repsMax, Integer restSeconds,
                                          Integer intensityPercent, String tempo, String notes) {
        return new PlannedExercise(id, workoutDayId, exerciseId, orderIndex, sets, repsMin,
                repsMax, restSeconds, intensityPercent, tempo, notes);
    }
}
