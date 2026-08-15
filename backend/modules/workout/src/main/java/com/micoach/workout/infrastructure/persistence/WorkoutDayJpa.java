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

import java.time.Instant;

/**
 * Entidad JPA de la tabla {@code workout_workout_days}.
 */
@Entity
@Table(name = "workout_workout_days")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutDayJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "workout_id", nullable = false)
    private Long workoutId;

    @Column(name = "day_index", nullable = false)
    private Short dayIndex;

    @Column(name = "name")
    private String name;

    @Column(name = "is_rest_day", nullable = false)
    private boolean restDay;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
