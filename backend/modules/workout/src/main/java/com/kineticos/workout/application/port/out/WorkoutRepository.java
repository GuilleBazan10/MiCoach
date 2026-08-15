package com.kineticos.workout.application.port.out;

import com.kineticos.workout.application.port.in.WorkoutUseCase.ExerciseFilter;
import com.kineticos.workout.domain.*;
import java.util.List;
import java.util.Optional;

public interface WorkoutRepository {
    List<Muscle> findMuscles();
    List<Exercise> findExercises(ExerciseFilter filter);
    Optional<Exercise> findExerciseById(Long exerciseId);
    List<Workout> findWorkoutsByUser(Long userId);
    List<Workout> findTemplates();
    Optional<Workout> findWorkoutById(Long workoutId);
    Workout saveWorkout(Workout workout);
    void deleteWorkout(Long workoutId);
    List<WorkoutSession> findSessionsByUser(Long userId);
    Optional<WorkoutSession> findSessionById(Long sessionId);
    WorkoutSession saveSession(WorkoutSession session);
    SessionExercise saveSessionExercise(SessionExercise exercise);
}
