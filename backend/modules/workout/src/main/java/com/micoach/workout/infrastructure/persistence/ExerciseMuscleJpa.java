package com.micoach.workout.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Entidad JPA de la tabla junction {@code workout_exercise_muscles}.
 */
@Entity
@Table(name = "workout_exercise_muscles")
@IdClass(ExerciseMuscleId.class)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseMuscleJpa {

    @Id
    @Column(name = "exercise_id")
    private Long exerciseId;

    @Id
    @Column(name = "muscle_id")
    private Long muscleId;

    @Id
    @Column(name = "role")
    private String role;
}
