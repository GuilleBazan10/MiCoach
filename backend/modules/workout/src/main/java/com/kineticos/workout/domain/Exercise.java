package com.kineticos.workout.domain;

import lombok.Getter;

import java.time.Instant;
import java.util.List;

/**
 * Catálogo de ejercicios (tabla workout_exercises), con sus músculos involucrados.
 */
@Getter
public class Exercise {

    private final Long id;
    private final String name;
    private final String description;
    private final String category;
    private final List<String> equipment;
    private final String difficulty;
    private final String instructions;
    private final String videoUrl;
    private final String imageUrl;
    private final boolean aiGenerated;
    private final boolean active;
    private final List<ExerciseMuscle> muscles;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Exercise(Long id, String name, String description, String category,
                     List<String> equipment, String difficulty, String instructions,
                     String videoUrl, String imageUrl, boolean aiGenerated, boolean active,
                     List<ExerciseMuscle> muscles, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.equipment = equipment == null ? List.of() : equipment;
        this.difficulty = difficulty;
        this.instructions = instructions;
        this.videoUrl = videoUrl;
        this.imageUrl = imageUrl;
        this.aiGenerated = aiGenerated;
        this.active = active;
        this.muscles = muscles == null ? List.of() : muscles;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Exercise restore(Long id, String name, String description, String category,
                                   List<String> equipment, String difficulty, String instructions,
                                   String videoUrl, String imageUrl, boolean aiGenerated,
                                   boolean active, List<ExerciseMuscle> muscles,
                                   Instant createdAt, Instant updatedAt) {
        return new Exercise(id, name, description, category, equipment, difficulty, instructions,
                videoUrl, imageUrl, aiGenerated, active, muscles, createdAt, updatedAt);
    }
}
