package com.kineticos.workout.infrastructure.persistence;

import com.kineticos.workout.application.port.in.WorkoutUseCase.ExerciseFilter;
import com.kineticos.workout.application.port.out.WorkoutRepository;
import com.kineticos.workout.domain.Exercise;
import com.kineticos.workout.domain.ExerciseMuscle;
import com.kineticos.workout.domain.Muscle;
import com.kineticos.workout.domain.PlannedExercise;
import com.kineticos.workout.domain.SessionExercise;
import com.kineticos.workout.domain.Workout;
import com.kineticos.workout.domain.WorkoutDay;
import com.kineticos.workout.domain.WorkoutSession;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Adaptador JPA del puerto {@link WorkoutRepository}. Cada rutina/sesión es un agregado:
 * este adaptador orquesta la persistencia de las tablas hijas (días, ejercicios) que
 * los mappers de {@link WorkoutMappers} solo convierten fila a fila.
 */
@Component
public class WorkoutRepositoryAdapter implements WorkoutRepository {

    private final MuscleJpaRepository muscleRepository;
    private final ExerciseJpaRepository exerciseRepository;
    private final ExerciseMuscleJpaRepository exerciseMuscleRepository;
    private final WorkoutJpaRepository workoutRepository;
    private final WorkoutDayJpaRepository workoutDayRepository;
    private final PlannedExerciseJpaRepository plannedExerciseRepository;
    private final WorkoutSessionJpaRepository sessionRepository;
    private final SessionExerciseJpaRepository sessionExerciseRepository;

    public WorkoutRepositoryAdapter(MuscleJpaRepository muscleRepository,
                                    ExerciseJpaRepository exerciseRepository,
                                    ExerciseMuscleJpaRepository exerciseMuscleRepository,
                                    WorkoutJpaRepository workoutRepository,
                                    WorkoutDayJpaRepository workoutDayRepository,
                                    PlannedExerciseJpaRepository plannedExerciseRepository,
                                    WorkoutSessionJpaRepository sessionRepository,
                                    SessionExerciseJpaRepository sessionExerciseRepository) {
        this.muscleRepository = muscleRepository;
        this.exerciseRepository = exerciseRepository;
        this.exerciseMuscleRepository = exerciseMuscleRepository;
        this.workoutRepository = workoutRepository;
        this.workoutDayRepository = workoutDayRepository;
        this.plannedExerciseRepository = plannedExerciseRepository;
        this.sessionRepository = sessionRepository;
        this.sessionExerciseRepository = sessionExerciseRepository;
    }

    // ------------------------- Catálogo -------------------------

    @Override
    public List<Muscle> findMuscles() {
        return muscleRepository.findAllByOrderByMuscleGroupAscNameAsc().stream()
                .map(MuscleMapper::toDomain).toList();
    }

    @Override
    public List<Exercise> findExercises(ExerciseFilter filter) {
        List<ExerciseJpa> all = exerciseRepository.findByActiveTrue();
        Map<Long, List<ExerciseMuscle>> musclesByExercise =
                loadExerciseMuscles(all.stream().map(ExerciseJpa::getId).toList());

        return all.stream()
                .filter(e -> filter.category() == null || filter.category().equalsIgnoreCase(e.getCategory()))
                .filter(e -> filter.difficulty() == null || filter.difficulty().equalsIgnoreCase(e.getDifficulty()))
                .filter(e -> filter.search() == null
                        || e.getName().toLowerCase().contains(filter.search().toLowerCase()))
                .filter(e -> filter.muscleId() == null
                        || musclesByExercise.getOrDefault(e.getId(), List.of()).stream()
                                .anyMatch(m -> m.getMuscleId().equals(filter.muscleId())))
                .map(e -> ExerciseMapper.toDomain(e, musclesByExercise.getOrDefault(e.getId(), List.of())))
                .toList();
    }

    @Override
    public Optional<Exercise> findExerciseById(Long exerciseId) {
        return exerciseRepository.findById(exerciseId)
                .map(e -> ExerciseMapper.toDomain(e,
                        loadExerciseMuscles(List.of(exerciseId)).getOrDefault(exerciseId, List.of())));
    }

    private Map<Long, List<ExerciseMuscle>> loadExerciseMuscles(List<Long> exerciseIds) {
        if (exerciseIds.isEmpty()) {
            return Map.of();
        }
        List<ExerciseMuscleJpa> links = exerciseMuscleRepository.findByExerciseIdIn(exerciseIds);
        if (links.isEmpty()) {
            return Map.of();
        }
        Set<Long> muscleIds = links.stream().map(ExerciseMuscleJpa::getMuscleId).collect(Collectors.toSet());
        Map<Long, MuscleJpa> musclesById = muscleRepository.findAllById(muscleIds).stream()
                .collect(Collectors.toMap(MuscleJpa::getId, m -> m));

        return links.stream().collect(Collectors.groupingBy(ExerciseMuscleJpa::getExerciseId,
                LinkedHashMap::new,
                Collectors.mapping(link -> {
                    MuscleJpa muscle = musclesById.get(link.getMuscleId());
                    return ExerciseMuscle.restore(link.getMuscleId(), muscle.getCode(), muscle.getName(),
                            link.getRole());
                }, Collectors.toList())));
    }

    // ------------------------- Rutinas -------------------------

    @Override
    public List<Workout> findWorkoutsByUser(Long userId) {
        return workoutRepository.findByUserId(userId).stream()
                .map(jpa -> WorkoutMapper.toDomain(jpa, loadDays(jpa.getId()))).toList();
    }

    @Override
    public List<Workout> findTemplates() {
        return workoutRepository.findByTemplateTrue().stream()
                .map(jpa -> WorkoutMapper.toDomain(jpa, loadDays(jpa.getId()))).toList();
    }

    @Override
    public Optional<Workout> findWorkoutById(Long workoutId) {
        return workoutRepository.findById(workoutId)
                .map(jpa -> WorkoutMapper.toDomain(jpa, loadDays(jpa.getId())));
    }

    @Override
    public Workout saveWorkout(Workout workout) {
        WorkoutJpa savedWorkout = workoutRepository.save(WorkoutMapper.toJpa(workout));

        // Estrategia "replace": se descartan los días/ejercicios previos y se recrean.
        // El ON DELETE CASCADE de workout_workout_exercises limpia las prescripciones.
        // flush() fuerza el DELETE antes de los INSERT siguientes (si no, Hibernate los
        // reordena y viola uq_workout_day al reinsertar el mismo day_index).
        workoutDayRepository.deleteByWorkoutId(savedWorkout.getId());
        workoutDayRepository.flush();
        for (WorkoutDay day : workout.getDays()) {
            WorkoutDayJpa savedDay = workoutDayRepository.save(WorkoutDayMapper.toJpa(day, savedWorkout.getId()));
            for (PlannedExercise exercise : day.getExercises()) {
                plannedExerciseRepository.save(PlannedExerciseMapper.toJpa(exercise, savedDay.getId()));
            }
        }
        return WorkoutMapper.toDomain(savedWorkout, loadDays(savedWorkout.getId()));
    }

    @Override
    public void deleteWorkout(Long workoutId) {
        workoutDayRepository.deleteByWorkoutId(workoutId);
        workoutRepository.deleteById(workoutId);
    }

    private List<WorkoutDay> loadDays(Long workoutId) {
        List<WorkoutDayJpa> dayJpas = workoutDayRepository.findByWorkoutIdOrderByDayIndexAsc(workoutId);
        if (dayJpas.isEmpty()) {
            return List.of();
        }
        List<Long> dayIds = dayJpas.stream().map(WorkoutDayJpa::getId).toList();
        Map<Long, List<PlannedExercise>> exercisesByDay = plannedExerciseRepository
                .findByWorkoutDayIdInOrderByOrderIndexAsc(dayIds).stream()
                .collect(Collectors.groupingBy(PlannedExerciseJpa::getWorkoutDayId, LinkedHashMap::new,
                        Collectors.mapping(PlannedExerciseMapper::toDomain, Collectors.toList())));

        return dayJpas.stream()
                .map(d -> WorkoutDayMapper.toDomain(d, exercisesByDay.getOrDefault(d.getId(), List.of())))
                .toList();
    }

    // ------------------------- Sesiones -------------------------

    @Override
    public List<WorkoutSession> findSessionsByUser(Long userId) {
        return sessionRepository.findByUserIdOrderByStartedAtDesc(userId).stream()
                .map(jpa -> WorkoutSessionMapper.toDomain(jpa, loadSessionExercises(jpa.getId()))).toList();
    }

    @Override
    public Optional<WorkoutSession> findSessionById(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .map(jpa -> WorkoutSessionMapper.toDomain(jpa, loadSessionExercises(jpa.getId())));
    }

    @Override
    public WorkoutSession saveSession(WorkoutSession session) {
        WorkoutSessionJpa saved = sessionRepository.save(WorkoutSessionMapper.toJpa(session));
        return WorkoutSessionMapper.toDomain(saved, loadSessionExercises(saved.getId()));
    }

    @Override
    public SessionExercise saveSessionExercise(SessionExercise exercise) {
        return SessionExerciseMapper.toDomain(
                sessionExerciseRepository.save(SessionExerciseMapper.toJpa(exercise)));
    }

    private List<SessionExercise> loadSessionExercises(Long sessionId) {
        return sessionExerciseRepository.findBySessionIdOrderByIdAsc(sessionId).stream()
                .map(SessionExerciseMapper::toDomain).toList();
    }
}
