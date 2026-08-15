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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;

/**
 * Entidad JPA de la tabla {@code workout_exercises} (catálogo).
 */
@Entity
@Table(name = "workout_exercises")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "equipment")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> equipment;

    @Column(name = "difficulty", nullable = false)
    private String difficulty;

    @Column(name = "instructions")
    private String instructions;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "measurement_type", nullable = false)
    private String measurementType;

    @Column(name = "is_ai_generated", nullable = false)
    private boolean aiGenerated;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
