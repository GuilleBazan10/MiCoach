package com.micoach.workout.infrastructure.persistence;

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

/**
 * Entidad JPA de la tabla {@code workout_session_exercises} (ejecución real por ejercicio).
 */
@Entity
@Table(name = "workout_session_exercises")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionExerciseJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "workout_exercise_id")
    private Long workoutExerciseId;

    @Column(name = "exercise_id", nullable = false)
    private Long exerciseId;

    @Column(name = "sets_done", nullable = false)
    private Short setsDone;

    @Column(name = "weight_kg")
    private BigDecimal weightKg;

    @Column(name = "reps")
    private Short reps;

    @Column(name = "rpe")
    private Short rpe;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "distance_meters")
    private Integer distanceMeters;

    @Column(name = "notes")
    private String notes;
}
