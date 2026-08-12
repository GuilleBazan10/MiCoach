package com.kineticos.workout.infrastructure.persistence;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Clave compuesta de {@link ExerciseMuscleJpa} (exercise_id, muscle_id, role).
 */
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseMuscleId implements Serializable {

    private Long exerciseId;
    private Long muscleId;
    private String role;
}
