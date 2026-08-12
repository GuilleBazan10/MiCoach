package com.kineticos.workout.domain;

import lombok.Getter;

/**
 * Músculo involucrado en un ejercicio, con su rol (tabla workout_exercise_muscles).
 * Se devuelve embebido en {@link Exercise}, incluye datos del músculo para no forzar
 * al caller a resolverlo aparte.
 */
@Getter
public class ExerciseMuscle {

    private final Long muscleId;
    private final String muscleCode;
    private final String muscleName;
    private final String role;

    private ExerciseMuscle(Long muscleId, String muscleCode, String muscleName, String role) {
        this.muscleId = muscleId;
        this.muscleCode = muscleCode;
        this.muscleName = muscleName;
        this.role = role;
    }

    public static ExerciseMuscle restore(Long muscleId, String muscleCode, String muscleName, String role) {
        return new ExerciseMuscle(muscleId, muscleCode, muscleName, role);
    }
}
