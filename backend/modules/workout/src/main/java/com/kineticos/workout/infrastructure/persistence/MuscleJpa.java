package com.kineticos.workout.infrastructure.persistence;

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
 * Entidad JPA de la tabla {@code workout_muscles} (catálogo).
 */
@Entity
@Table(name = "workout_muscles")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MuscleJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "muscle_group", nullable = false)
    private String muscleGroup;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
