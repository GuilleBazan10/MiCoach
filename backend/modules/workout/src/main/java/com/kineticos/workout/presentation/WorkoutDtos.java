package com.kineticos.workout.presentation;

import com.kineticos.workout.domain.Exercise;
import com.kineticos.workout.domain.ExerciseMuscle;
import com.kineticos.workout.domain.Muscle;
import com.kineticos.workout.domain.PlannedExercise;
import com.kineticos.workout.domain.SessionExercise;
import com.kineticos.workout.domain.Workout;
import com.kineticos.workout.domain.WorkoutDay;
import com.kineticos.workout.domain.WorkoutSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTOs del módulo workout. Cada verbose class es un contrato de entrada/salida.
 */
public final class WorkoutDtos {

    private WorkoutDtos() {
    }

    // ------------------------- Catálogo -------------------------

    public record MuscleResponse(Long id, String code, String name, String muscleGroup) {

        static MuscleResponse from(Muscle m) {
            return new MuscleResponse(m.getId(), m.getCode(), m.getName(), m.getMuscleGroup());
        }
    }

    public record ExerciseMuscleResponse(Long muscleId, String muscleCode, String muscleName, String role) {

        static ExerciseMuscleResponse from(ExerciseMuscle m) {
            return new ExerciseMuscleResponse(m.getMuscleId(), m.getMuscleCode(), m.getMuscleName(), m.getRole());
        }
    }

    public record ExerciseResponse(Long id, String name, String description, String category,
                                   List<String> equipment, String difficulty, String instructions,
                                   String videoUrl, String imageUrl, String measurementType,
                                   boolean aiGenerated, List<ExerciseMuscleResponse> muscles) {

        static ExerciseResponse from(Exercise e) {
            return new ExerciseResponse(e.getId(), e.getName(), e.getDescription(), e.getCategory(),
                    e.getEquipment(), e.getDifficulty(), e.getInstructions(), e.getVideoUrl(),
                    e.getImageUrl(), e.getMeasurementType(), e.isAiGenerated(),
                    e.getMuscles().stream().map(ExerciseMuscleResponse::from).toList());
        }
    }

    // ------------------------- Rutinas -------------------------

    public record PlannedExerciseResponse(Long id, Long exerciseId, Integer orderIndex, Integer sets,
                                          Integer repsMin, Integer repsMax, Integer restSeconds,
                                          Integer intensityPercent, String tempo, String notes) {

        static PlannedExerciseResponse from(PlannedExercise p) {
            return new PlannedExerciseResponse(p.getId(), p.getExerciseId(), p.getOrderIndex(),
                    p.getSets(), p.getRepsMin(), p.getRepsMax(), p.getRestSeconds(),
                    p.getIntensityPercent(), p.getTempo(), p.getNotes());
        }
    }

    public record PlannedExerciseRequest(@NotNull Long exerciseId, @NotNull @Min(1) Integer orderIndex,
                                         @Min(1) Integer sets, Integer repsMin, Integer repsMax,
                                         Integer restSeconds,
                                         @Min(1) @Max(100) Integer intensityPercent,
                                         @Size(max = 30) String tempo, @Size(max = 500) String notes) {
    }

    public record WorkoutDayResponse(Long id, Integer dayIndex, String name, boolean restDay,
                                     List<PlannedExerciseResponse> exercises) {

        static WorkoutDayResponse from(WorkoutDay d) {
            return new WorkoutDayResponse(d.getId(), d.getDayIndex(), d.getName(), d.isRestDay(),
                    d.getExercises().stream().map(PlannedExerciseResponse::from).toList());
        }
    }

    public record WorkoutDayRequest(@NotNull @Min(1) Integer dayIndex, @Size(max = 100) String name,
                                    boolean restDay, @Valid List<PlannedExerciseRequest> exercises) {
    }

    public record WorkoutResponse(Long id, Long userId, String name, String description,
                                  String objective, String level, Integer durationWeeks,
                                  boolean template, boolean aiGenerated, String status,
                                  List<WorkoutDayResponse> days) {

        static WorkoutResponse from(Workout w) {
            return new WorkoutResponse(w.getId(), w.getUserId(), w.getName(), w.getDescription(),
                    w.getObjective(), w.getLevel(), w.getDurationWeeks(), w.isTemplate(),
                    w.isAiGenerated(), w.getStatus(),
                    w.getDays().stream().map(WorkoutDayResponse::from).toList());
        }
    }

    public record WorkoutRequest(@NotBlank @Size(max = 200) String name,
                                 @Size(max = 1000) String description,
                                 @Size(max = 30) String objective, @Size(max = 15) String level,
                                 Integer durationWeeks,
                                 @NotEmpty @Valid List<WorkoutDayRequest> days) {
    }

    public record GenerateWorkoutRequest(@NotBlank @Size(max = 1000) String goal) {
    }

    // ------------------------- Sesiones -------------------------

    public record SessionExerciseResponse(Long id, Long workoutExerciseId, Long exerciseId,
                                          Integer setsDone, BigDecimal weightKg, Integer reps,
                                          Integer rpe, Integer durationSeconds,
                                          Integer distanceMeters, String notes) {

        static SessionExerciseResponse from(SessionExercise e) {
            return new SessionExerciseResponse(e.getId(), e.getWorkoutExerciseId(), e.getExerciseId(),
                    e.getSetsDone(), e.getWeightKg(), e.getReps(), e.getRpe(), e.getDurationSeconds(),
                    e.getDistanceMeters(), e.getNotes());
        }
    }

    public record WorkoutSessionResponse(Long id, Long workoutId, Long workoutDayId, String status,
                                         java.time.Instant startedAt, java.time.Instant completedAt,
                                         Integer durationSeconds, String notes,
                                         List<SessionExerciseResponse> exercises) {

        static WorkoutSessionResponse from(WorkoutSession s) {
            return new WorkoutSessionResponse(s.getId(), s.getWorkoutId(), s.getWorkoutDayId(),
                    s.getStatus(), s.getStartedAt(), s.getCompletedAt(), s.getDurationSeconds(),
                    s.getNotes(), s.getExercises().stream().map(SessionExerciseResponse::from).toList());
        }
    }

    public record StartSessionRequest(Long workoutId, Long workoutDayId) {
    }

    public record CompleteSessionRequest(@Positive Integer durationSeconds,
                                         @Size(max = 500) String notes) {
    }

    public record AbortSessionRequest(@Size(max = 500) String notes) {
    }

    public record SessionExerciseRequest(Long workoutExerciseId, @NotNull Long exerciseId,
                                         Integer setsDone, BigDecimal weightKg, Integer reps,
                                         @Min(1) @Max(10) Integer rpe, Integer durationSeconds,
                                         Integer distanceMeters, @Size(max = 500) String notes) {
    }
}
