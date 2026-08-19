package com.micoach.workout.application.port.in;

import com.micoach.workout.domain.Exercise;
import com.micoach.workout.domain.Muscle;
import com.micoach.workout.domain.SessionExercise;
import com.micoach.workout.domain.Workout;
import com.micoach.workout.domain.WorkoutSession;

import java.math.BigDecimal;
import java.util.List;

/**
 * Puerto de entrada del módulo workout (catálogo, rutinas y sesiones).
 */
public interface WorkoutUseCase {

    // ------------------------- Catálogo -------------------------

    List<Muscle> listMuscles();

    List<Exercise> listExercises(ExerciseFilter filter);

    Exercise getExercise(Long exerciseId);

    // ------------------------- Rutinas -------------------------

    List<Workout> listWorkouts(Long userId, boolean templates);

    Workout getWorkout(Long userId, Long workoutId);

    Workout createWorkout(Long userId, WorkoutData data);

    /**
     * Genera una rutina con IA (LangChain4j + Ollama, ver módulo {@code ai}) a partir de
     * un pedido en lenguaje natural, la crea como propia del usuario ({@code aiGenerated
     * = true}) y la devuelve ya persistida.
     */
    Workout generateWorkout(Long userId, String goal);

    Workout updateWorkout(Long userId, Long workoutId, WorkoutData data);

    void deleteWorkout(Long userId, Long workoutId);

    // ------------------------- Sesiones -------------------------

    List<WorkoutSession> listSessions(Long userId);

    WorkoutSession getSession(Long userId, Long sessionId);

    WorkoutSession startSession(Long userId, StartSessionData data);

    WorkoutSession completeSession(Long userId, Long sessionId, CompleteSessionData data);

    WorkoutSession abortSession(Long userId, Long sessionId, String notes);

    SessionExercise logSessionExercise(Long userId, Long sessionId, SessionExerciseData data);

    record ExerciseFilter(String category, String difficulty, Long muscleId, String search) {
    }

    record WorkoutData(String name, String description, String objective, String level,
                       Integer durationWeeks, List<WorkoutDayData> days, Long generationLogId) {
    }

    record WorkoutDayData(Integer dayIndex, String name, boolean restDay,
                          List<PlannedExerciseData> exercises) {
    }

    record PlannedExerciseData(Long exerciseId, Integer orderIndex, Integer sets, Integer repsMin,
                               Integer repsMax, Integer restSeconds, Integer intensityPercent,
                               String tempo, String notes) {
    }

    record StartSessionData(Long workoutId, Long workoutDayId) {
    }

    record CompleteSessionData(Integer durationSeconds, String notes) {
    }

    record SessionExerciseData(Long workoutExerciseId, Long exerciseId, Integer setsDone,
                               BigDecimal weightKg, Integer reps, Integer rpe,
                               Integer durationSeconds, Integer distanceMeters, String notes) {
    }
}
