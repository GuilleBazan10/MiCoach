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

/**
 * Entidad JPA de la tabla {@code workout_workout_exercises} (prescripción por día).
 */
@Entity
@Table(name = "workout_workout_exercises")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlannedExerciseJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "workout_day_id", nullable = false)
    private Long workoutDayId;

    @Column(name = "exercise_id", nullable = false)
    private Long exerciseId;

    @Column(name = "order_index", nullable = false)
    private Short orderIndex;

    @Column(name = "sets", nullable = false)
    private Short sets;

    @Column(name = "reps_min")
    private Short repsMin;

    @Column(name = "reps_max")
    private Short repsMax;

    @Column(name = "rest_seconds")
    private Short restSeconds;

    @Column(name = "intensity_percent")
    private Short intensityPercent;

    @Column(name = "tempo")
    private String tempo;

    @Column(name = "notes")
    private String notes;
}
