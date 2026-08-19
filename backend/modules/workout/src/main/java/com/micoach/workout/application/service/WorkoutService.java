package com.micoach.workout.application.service;

import com.micoach.ai.application.port.in.AiUseCase;
import com.micoach.shared.error.DomainException;
import com.micoach.shared.error.ErrorCode;
import com.micoach.workout.application.port.in.WorkoutUseCase;
import com.micoach.workout.application.port.out.WorkoutRepository;
import com.micoach.workout.domain.Exercise;
import com.micoach.workout.domain.Muscle;
import com.micoach.workout.domain.PlannedExercise;
import com.micoach.workout.domain.SessionExercise;
import com.micoach.workout.domain.Workout;
import com.micoach.workout.domain.WorkoutDay;
import com.micoach.workout.domain.WorkoutSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación de casos de uso del módulo workout. Depende solo del puerto de salida.
 */
@Service
public class WorkoutService implements WorkoutUseCase {

    private final WorkoutRepository repository;
    private final WorkoutAiGenerator aiGenerator;
    private final AiUseCase aiUseCase;

    public WorkoutService(WorkoutRepository repository, WorkoutAiGenerator aiGenerator, AiUseCase aiUseCase) {
        this.repository = repository;
        this.aiGenerator = aiGenerator;
        this.aiUseCase = aiUseCase;
    }

    // ------------------------- Catálogo -------------------------

    @Override
    @Transactional(readOnly = true)
    public List<Muscle> listMuscles() {
        return repository.findMuscles();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Exercise> listExercises(ExerciseFilter filter) {
        return repository.findExercises(filter);
    }

    @Override
    @Transactional(readOnly = true)
    public Exercise getExercise(Long exerciseId) {
        return repository.findExerciseById(exerciseId)
                .orElseThrow(() -> new DomainException(404, ErrorCode.NOT_FOUND,
                        "Ejercicio no encontrado"));
    }

    // ------------------------- Rutinas -------------------------

    @Override
    @Transactional(readOnly = true)
    public List<Workout> listWorkouts(Long userId, boolean templates) {
        return templates ? repository.findTemplates() : repository.findWorkoutsByUser(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Workout getWorkout(Long userId, Long workoutId) {
        return requireAccessibleWorkout(userId, workoutId);
    }

    @Override
    @Transactional
    public Workout createWorkout(Long userId, WorkoutData data) {
        Workout workout = Workout.create(userId, data.name(), data.description(), data.objective(),
                data.level(), data.durationWeeks(), toDays(data.days()));
        return repository.saveWorkout(workout);
    }

    @Override
    @Transactional
    public Workout updateWorkout(Long userId, Long workoutId, WorkoutData data) {
        Workout workout = requireOwnedWorkout(userId, workoutId);
        workout.update(data.name(), data.description(), data.objective(), data.level(),
                data.durationWeeks(), toDays(data.days()));
        return repository.saveWorkout(workout);
    }

    @Override
    @Transactional
    public Workout generateWorkout(Long userId, String goal) {
        List<Exercise> catalog = repository.findExercises(new ExerciseFilter(null, null, null, null));
        WorkoutData data = aiGenerator.generate(userId, goal, catalog);
        Workout workout = Workout.createAiGenerated(userId, data.name(), data.description(), data.objective(),
                data.level(), data.durationWeeks(), toDays(data.days()), data.generationLogId());
        return repository.saveWorkout(workout);
    }

    @Override
    @Transactional
    public void deleteWorkout(Long userId, Long workoutId) {
        Workout workout = requireOwnedWorkout(userId, workoutId);
        // Cierra el loop de memoria persistente: si el usuario descarta una rutina
        // generada por IA sin haberla usado, esa señal queda registrada para la próxima
        // generación de este mismo usuario (ver WorkoutAiGenerator.buildFeedbackHistory).
        if (workout.isAiGenerated() && workout.getGenerationLogId() != null) {
            aiUseCase.recordFeedback(workout.getGenerationLogId(), "discarded");
        }
        repository.deleteWorkout(workoutId);
    }

    private List<WorkoutDay> toDays(List<WorkoutDayData> days) {
        if (days == null) {
            return List.of();
        }
        return days.stream()
                .map(d -> WorkoutDay.create(d.dayIndex(), d.name(), d.restDay(), toExercises(d.exercises())))
                .toList();
    }

    private List<PlannedExercise> toExercises(List<PlannedExerciseData> exercises) {
        if (exercises == null) {
            return List.of();
        }
        return exercises.stream()
                .map(e -> PlannedExercise.create(e.exerciseId(), e.orderIndex(), e.sets(),
                        e.repsMin(), e.repsMax(), e.restSeconds(), e.intensityPercent(), e.tempo(),
                        e.notes()))
                .toList();
    }

    private Workout requireAccessibleWorkout(Long userId, Long workoutId) {
        Workout workout = repository.findWorkoutById(workoutId)
                .orElseThrow(() -> new DomainException(404, ErrorCode.NOT_FOUND,
                        "Rutina no encontrada"));
        if (!workout.isTemplate() && !workout.belongsTo(userId)) {
            throw new DomainException(404, ErrorCode.NOT_FOUND, "Rutina no encontrada");
        }
        return workout;
    }

    private Workout requireOwnedWorkout(Long userId, Long workoutId) {
        Workout workout = repository.findWorkoutById(workoutId)
                .orElseThrow(() -> new DomainException(404, ErrorCode.NOT_FOUND,
                        "Rutina no encontrada"));
        if (!workout.belongsTo(userId)) {
            throw new DomainException(404, ErrorCode.NOT_FOUND, "Rutina no encontrada");
        }
        return workout;
    }

    // ------------------------- Sesiones -------------------------

    @Override
    @Transactional(readOnly = true)
    public List<WorkoutSession> listSessions(Long userId) {
        return repository.findSessionsByUser(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkoutSession getSession(Long userId, Long sessionId) {
        return requireOwnedSession(userId, sessionId);
    }

    @Override
    @Transactional
    public WorkoutSession startSession(Long userId, StartSessionData data) {
        if (data.workoutId() != null) {
            requireAccessibleWorkout(userId, data.workoutId());
        }
        return repository.saveSession(WorkoutSession.start(userId, data.workoutId(), data.workoutDayId()));
    }

    @Override
    @Transactional
    public WorkoutSession completeSession(Long userId, Long sessionId, CompleteSessionData data) {
        WorkoutSession session = requireOwnedSession(userId, sessionId);
        session.complete(data.durationSeconds(), data.notes());
        return repository.saveSession(session);
    }

    @Override
    @Transactional
    public WorkoutSession abortSession(Long userId, Long sessionId, String notes) {
        WorkoutSession session = requireOwnedSession(userId, sessionId);
        session.abort(notes);
        return repository.saveSession(session);
    }

    @Override
    @Transactional
    public SessionExercise logSessionExercise(Long userId, Long sessionId, SessionExerciseData data) {
        requireOwnedSession(userId, sessionId);
        return repository.saveSessionExercise(SessionExercise.create(sessionId,
                data.workoutExerciseId(), data.exerciseId(), data.setsDone(), data.weightKg(),
                data.reps(), data.rpe(), data.durationSeconds(), data.distanceMeters(), data.notes()));
    }

    private WorkoutSession requireOwnedSession(Long userId, Long sessionId) {
        WorkoutSession session = repository.findSessionById(sessionId)
                .orElseThrow(() -> new DomainException(404, ErrorCode.NOT_FOUND,
                        "Sesión no encontrada"));
        if (!session.belongsTo(userId)) {
            throw new DomainException(404, ErrorCode.NOT_FOUND, "Sesión no encontrada");
        }
        return session;
    }
}
