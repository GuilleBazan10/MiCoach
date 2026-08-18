package com.micoach.workout.infrastructure.persistence;

import com.micoach.workout.domain.Exercise;
import com.micoach.workout.domain.ExerciseMuscle;
import com.micoach.workout.domain.Muscle;
import com.micoach.workout.domain.PlannedExercise;
import com.micoach.workout.domain.SessionExercise;
import com.micoach.workout.domain.Workout;
import com.micoach.workout.domain.WorkoutDay;
import com.micoach.workout.domain.WorkoutSession;

import java.util.List;

final class NumberConversions {

    private NumberConversions() {
    }

    static Integer intValue(Short v) {
        return v == null ? null : v.intValue();
    }

    static Short shortValue(Integer v) {
        return v == null ? null : v.shortValue();
    }
}

final class MuscleMapper {

    private MuscleMapper() {
    }

    static Muscle toDomain(MuscleJpa jpa) {
        return Muscle.restore(jpa.getId(), jpa.getCode(), jpa.getName(), jpa.getMuscleGroup());
    }
}

final class ExerciseMapper {

    private ExerciseMapper() {
    }

    static Exercise toDomain(ExerciseJpa jpa, List<ExerciseMuscle> muscles) {
        return Exercise.restore(jpa.getId(), jpa.getName(), jpa.getDescription(), jpa.getCategory(),
                jpa.getEquipment(), jpa.getDifficulty(), jpa.getInstructions(), jpa.getVideoUrl(),
                jpa.getImageUrl(), jpa.getImageUrlEnd(), jpa.getMeasurementType(), jpa.isAiGenerated(),
                jpa.isActive(), muscles, jpa.getCreatedAt(), jpa.getUpdatedAt());
    }
}

final class WorkoutMapper {

    private WorkoutMapper() {
    }

    static Workout toDomain(WorkoutJpa jpa, List<WorkoutDay> days) {
        return Workout.restore(jpa.getId(), jpa.getUserId(), jpa.getName(), jpa.getDescription(),
                jpa.getObjective(), jpa.getLevel(), NumberConversions.intValue(jpa.getDurationWeeks()),
                jpa.isTemplate(), jpa.isAiGenerated(), jpa.getStatus(), days, jpa.getCreatedAt(),
                jpa.getUpdatedAt());
    }

    static WorkoutJpa toJpa(Workout domain) {
        return WorkoutJpa.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .name(domain.getName())
                .description(domain.getDescription())
                .objective(domain.getObjective())
                .level(domain.getLevel())
                .durationWeeks(NumberConversions.shortValue(domain.getDurationWeeks()))
                .template(domain.isTemplate())
                .aiGenerated(domain.isAiGenerated())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}

final class WorkoutDayMapper {

    private WorkoutDayMapper() {
    }

    static WorkoutDay toDomain(WorkoutDayJpa jpa, List<PlannedExercise> exercises) {
        return WorkoutDay.restore(jpa.getId(), NumberConversions.intValue(jpa.getDayIndex()),
                jpa.getName(), jpa.isRestDay(), exercises);
    }

    static WorkoutDayJpa toJpa(WorkoutDay domain, Long workoutId) {
        return WorkoutDayJpa.builder()
                .workoutId(workoutId)
                .dayIndex(NumberConversions.shortValue(domain.getDayIndex()))
                .name(domain.getName())
                .restDay(domain.isRestDay())
                .createdAt(java.time.Instant.now())
                .build();
    }
}

final class PlannedExerciseMapper {

    private PlannedExerciseMapper() {
    }

    static PlannedExercise toDomain(PlannedExerciseJpa jpa) {
        return PlannedExercise.restore(jpa.getId(), jpa.getWorkoutDayId(), jpa.getExerciseId(),
                NumberConversions.intValue(jpa.getOrderIndex()), NumberConversions.intValue(jpa.getSets()),
                NumberConversions.intValue(jpa.getRepsMin()), NumberConversions.intValue(jpa.getRepsMax()),
                NumberConversions.intValue(jpa.getRestSeconds()),
                NumberConversions.intValue(jpa.getIntensityPercent()), jpa.getTempo(), jpa.getNotes());
    }

    static PlannedExerciseJpa toJpa(PlannedExercise domain, Long workoutDayId) {
        return PlannedExerciseJpa.builder()
                .workoutDayId(workoutDayId)
                .exerciseId(domain.getExerciseId())
                .orderIndex(NumberConversions.shortValue(domain.getOrderIndex()))
                .sets(NumberConversions.shortValue(domain.getSets()))
                .repsMin(NumberConversions.shortValue(domain.getRepsMin()))
                .repsMax(NumberConversions.shortValue(domain.getRepsMax()))
                .restSeconds(NumberConversions.shortValue(domain.getRestSeconds()))
                .intensityPercent(NumberConversions.shortValue(domain.getIntensityPercent()))
                .tempo(domain.getTempo())
                .notes(domain.getNotes())
                .build();
    }
}

final class WorkoutSessionMapper {

    private WorkoutSessionMapper() {
    }

    static WorkoutSession toDomain(WorkoutSessionJpa jpa, List<SessionExercise> exercises) {
        return WorkoutSession.restore(jpa.getId(), jpa.getUserId(), jpa.getWorkoutId(),
                jpa.getWorkoutDayId(), jpa.getStatus(), jpa.getStartedAt(), jpa.getCompletedAt(),
                jpa.getDurationSeconds(), jpa.getNotes(), exercises, jpa.getCreatedAt(),
                jpa.getUpdatedAt());
    }

    static WorkoutSessionJpa toJpa(WorkoutSession domain) {
        return WorkoutSessionJpa.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .workoutId(domain.getWorkoutId())
                .workoutDayId(domain.getWorkoutDayId())
                .status(domain.getStatus())
                .startedAt(domain.getStartedAt())
                .completedAt(domain.getCompletedAt())
                .durationSeconds(domain.getDurationSeconds())
                .notes(domain.getNotes())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}

final class SessionExerciseMapper {

    private SessionExerciseMapper() {
    }

    static SessionExercise toDomain(SessionExerciseJpa jpa) {
        return SessionExercise.restore(jpa.getId(), jpa.getSessionId(), jpa.getWorkoutExerciseId(),
                jpa.getExerciseId(), NumberConversions.intValue(jpa.getSetsDone()), jpa.getWeightKg(),
                NumberConversions.intValue(jpa.getReps()), NumberConversions.intValue(jpa.getRpe()),
                jpa.getDurationSeconds(), jpa.getDistanceMeters(), jpa.getNotes());
    }

    static SessionExerciseJpa toJpa(SessionExercise domain) {
        return SessionExerciseJpa.builder()
                .id(domain.getId())
                .sessionId(domain.getSessionId())
                .workoutExerciseId(domain.getWorkoutExerciseId())
                .exerciseId(domain.getExerciseId())
                .setsDone(NumberConversions.shortValue(domain.getSetsDone()))
                .weightKg(domain.getWeightKg())
                .reps(NumberConversions.shortValue(domain.getReps()))
                .rpe(NumberConversions.shortValue(domain.getRpe()))
                .durationSeconds(domain.getDurationSeconds())
                .distanceMeters(domain.getDistanceMeters())
                .notes(domain.getNotes())
                .build();
    }
}
