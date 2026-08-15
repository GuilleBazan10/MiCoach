package com.micoach.workout.domain;

import lombok.Getter;

import java.util.List;

/**
 * Día de una rutina, con sus ejercicios prescritos (tabla workout_workout_days).
 */
@Getter
public class WorkoutDay {

    private final Long id;
    private final Integer dayIndex;
    private final String name;
    private final boolean restDay;
    private final List<PlannedExercise> exercises;

    private WorkoutDay(Long id, Integer dayIndex, String name, boolean restDay,
                       List<PlannedExercise> exercises) {
        this.id = id;
        this.dayIndex = dayIndex;
        this.name = name;
        this.restDay = restDay;
        this.exercises = exercises == null ? List.of() : exercises;
    }

    public static WorkoutDay create(Integer dayIndex, String name, boolean restDay,
                                    List<PlannedExercise> exercises) {
        return new WorkoutDay(null, dayIndex, name, restDay, exercises);
    }

    public static WorkoutDay restore(Long id, Integer dayIndex, String name, boolean restDay,
                                     List<PlannedExercise> exercises) {
        return new WorkoutDay(id, dayIndex, name, restDay, exercises);
    }
}
