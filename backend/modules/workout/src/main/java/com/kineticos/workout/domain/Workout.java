package com.kineticos.workout.domain;

import lombok.Getter;

import java.time.Instant;
import java.util.List;

/**
 * Rutina de entrenamiento (tabla workout_workouts), agregado con sus días y ejercicios.
 * {@code userId} nulo representa una plantilla global (admin/IA); las rutinas creadas por
 * el usuario a través de la API siempre llevan su {@code userId}.
 */
@Getter
public class Workout {

    private Long id;
    private final Long userId;
    private String name;
    private String description;
    private String objective;
    private String level;
    private Integer durationWeeks;
    private final boolean template;
    private final boolean aiGenerated;
    private String status;
    private List<WorkoutDay> days;
    private final Instant createdAt;
    private Instant updatedAt;

    private Workout(Long id, Long userId, String name, String description, String objective,
                    String level, Integer durationWeeks, boolean template, boolean aiGenerated,
                    String status, List<WorkoutDay> days, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.objective = objective;
        this.level = level;
        this.durationWeeks = durationWeeks;
        this.template = template;
        this.aiGenerated = aiGenerated;
        this.status = status;
        this.days = days == null ? List.of() : days;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Workout create(Long userId, String name, String description, String objective,
                                 String level, Integer durationWeeks, List<WorkoutDay> days) {
        Instant now = Instant.now();
        return new Workout(null, userId, name, description, objective, level, durationWeeks,
                false, false, "active", days, now, now);
    }

    public static Workout createAiGenerated(Long userId, String name, String description, String objective,
                                            String level, Integer durationWeeks, List<WorkoutDay> days) {
        Instant now = Instant.now();
        return new Workout(null, userId, name, description, objective, level, durationWeeks,
                false, true, "active", days, now, now);
    }

    public static Workout restore(Long id, Long userId, String name, String description,
                                  String objective, String level, Integer durationWeeks,
                                  boolean template, boolean aiGenerated, String status,
                                  List<WorkoutDay> days, Instant createdAt, Instant updatedAt) {
        return new Workout(id, userId, name, description, objective, level, durationWeeks,
                template, aiGenerated, status, days, createdAt, updatedAt);
    }

    public void update(String name, String description, String objective, String level,
                       Integer durationWeeks, List<WorkoutDay> days) {
        this.name = name;
        this.description = description;
        this.objective = objective;
        this.level = level;
        this.durationWeeks = durationWeeks;
        this.days = days == null ? List.of() : days;
        this.updatedAt = Instant.now();
    }

    public void archive() {
        this.status = "archived";
        this.updatedAt = Instant.now();
    }

    public boolean belongsTo(Long userId) {
        return this.userId != null && this.userId.equals(userId);
    }
}
