package com.micoach.workout.presentation;

import com.micoach.shared.security.AuthenticatedUser;
import com.micoach.workout.application.port.in.WorkoutUseCase;
import com.micoach.workout.application.port.in.WorkoutUseCase.CompleteSessionData;
import com.micoach.workout.application.port.in.WorkoutUseCase.ExerciseFilter;
import com.micoach.workout.application.port.in.WorkoutUseCase.PlannedExerciseData;
import com.micoach.workout.application.port.in.WorkoutUseCase.SessionExerciseData;
import com.micoach.workout.application.port.in.WorkoutUseCase.StartSessionData;
import com.micoach.workout.application.port.in.WorkoutUseCase.WorkoutData;
import com.micoach.workout.application.port.in.WorkoutUseCase.WorkoutDayData;
import com.micoach.workout.presentation.WorkoutDtos.AbortSessionRequest;
import com.micoach.workout.presentation.WorkoutDtos.CompleteSessionRequest;
import com.micoach.workout.presentation.WorkoutDtos.ExerciseResponse;
import com.micoach.workout.presentation.WorkoutDtos.GenerateWorkoutRequest;
import com.micoach.workout.presentation.WorkoutDtos.MuscleResponse;
import com.micoach.workout.presentation.WorkoutDtos.PlannedExerciseRequest;
import com.micoach.workout.presentation.WorkoutDtos.SessionExerciseRequest;
import com.micoach.workout.presentation.WorkoutDtos.SessionExerciseResponse;
import com.micoach.workout.presentation.WorkoutDtos.StartSessionRequest;
import com.micoach.workout.presentation.WorkoutDtos.WorkoutDayRequest;
import com.micoach.workout.presentation.WorkoutDtos.WorkoutRequest;
import com.micoach.workout.presentation.WorkoutDtos.WorkoutResponse;
import com.micoach.workout.presentation.WorkoutDtos.WorkoutSessionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Contratos REST del módulo workout (base path /api/v1/workouts). Todos requieren JWT
 * (configurado en app/security).
 */
@RestController
@RequestMapping("/api/v1/workouts")
public class WorkoutController {

    private final WorkoutUseCase useCase;

    public WorkoutController(WorkoutUseCase useCase) {
        this.useCase = useCase;
    }

    // ------------------------- Catálogo -------------------------

    @GetMapping("/muscles")
    public List<MuscleResponse> listMuscles() {
        return useCase.listMuscles().stream().map(MuscleResponse::from).toList();
    }

    @GetMapping("/exercises")
    public List<ExerciseResponse> listExercises(@RequestParam(required = false) String category,
                                                @RequestParam(required = false) String difficulty,
                                                @RequestParam(required = false) Long muscleId,
                                                @RequestParam(required = false) String search) {
        ExerciseFilter filter = new ExerciseFilter(category, difficulty, muscleId, search);
        return useCase.listExercises(filter).stream().map(ExerciseResponse::from).toList();
    }

    @GetMapping("/exercises/{exerciseId}")
    public ExerciseResponse getExercise(@PathVariable Long exerciseId) {
        return ExerciseResponse.from(useCase.getExercise(exerciseId));
    }

    // ------------------------- Rutinas -------------------------

    @GetMapping
    public List<WorkoutResponse> listWorkouts(@AuthenticationPrincipal AuthenticatedUser user,
                                              @RequestParam(defaultValue = "false") boolean templates) {
        return useCase.listWorkouts(user.id(), templates).stream().map(WorkoutResponse::from).toList();
    }

    @GetMapping("/{workoutId}")
    public WorkoutResponse getWorkout(@AuthenticationPrincipal AuthenticatedUser user,
                                      @PathVariable Long workoutId) {
        return WorkoutResponse.from(useCase.getWorkout(user.id(), workoutId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkoutResponse createWorkout(@AuthenticationPrincipal AuthenticatedUser user,
                                         @Valid @RequestBody WorkoutRequest request) {
        return WorkoutResponse.from(useCase.createWorkout(user.id(), toWorkoutData(request)));
    }

    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.CREATED)
    public WorkoutResponse generateWorkout(@AuthenticationPrincipal AuthenticatedUser user,
                                           @Valid @RequestBody GenerateWorkoutRequest request) {
        return WorkoutResponse.from(useCase.generateWorkout(user.id(), request.goal()));
    }

    @PutMapping("/{workoutId}")
    public WorkoutResponse updateWorkout(@AuthenticationPrincipal AuthenticatedUser user,
                                         @PathVariable Long workoutId,
                                         @Valid @RequestBody WorkoutRequest request) {
        return WorkoutResponse.from(useCase.updateWorkout(user.id(), workoutId, toWorkoutData(request)));
    }

    @DeleteMapping("/{workoutId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWorkout(@AuthenticationPrincipal AuthenticatedUser user,
                              @PathVariable Long workoutId) {
        useCase.deleteWorkout(user.id(), workoutId);
    }

    private WorkoutData toWorkoutData(WorkoutRequest request) {
        List<WorkoutDayData> days = request.days().stream().map(this::toDayData).toList();
        return new WorkoutData(request.name(), request.description(), request.objective(),
                request.level(), request.durationWeeks(), days);
    }

    private WorkoutDayData toDayData(WorkoutDayRequest request) {
        List<PlannedExerciseData> exercises = request.exercises() == null ? List.of()
                : request.exercises().stream().map(this::toExerciseData).toList();
        return new WorkoutDayData(request.dayIndex(), request.name(), request.restDay(), exercises);
    }

    private PlannedExerciseData toExerciseData(PlannedExerciseRequest request) {
        return new PlannedExerciseData(request.exerciseId(), request.orderIndex(), request.sets(),
                request.repsMin(), request.repsMax(), request.restSeconds(),
                request.intensityPercent(), request.tempo(), request.notes());
    }

    // ------------------------- Sesiones -------------------------

    @GetMapping("/sessions")
    public List<WorkoutSessionResponse> listSessions(@AuthenticationPrincipal AuthenticatedUser user) {
        return useCase.listSessions(user.id()).stream().map(WorkoutSessionResponse::from).toList();
    }

    @GetMapping("/sessions/{sessionId}")
    public WorkoutSessionResponse getSession(@AuthenticationPrincipal AuthenticatedUser user,
                                             @PathVariable Long sessionId) {
        return WorkoutSessionResponse.from(useCase.getSession(user.id(), sessionId));
    }

    @PostMapping("/sessions")
    @ResponseStatus(HttpStatus.CREATED)
    public WorkoutSessionResponse startSession(@AuthenticationPrincipal AuthenticatedUser user,
                                               @RequestBody StartSessionRequest request) {
        StartSessionData data = new StartSessionData(request.workoutId(), request.workoutDayId());
        return WorkoutSessionResponse.from(useCase.startSession(user.id(), data));
    }

    @PutMapping("/sessions/{sessionId}/complete")
    public WorkoutSessionResponse completeSession(@AuthenticationPrincipal AuthenticatedUser user,
                                                  @PathVariable Long sessionId,
                                                  @Valid @RequestBody CompleteSessionRequest request) {
        CompleteSessionData data = new CompleteSessionData(request.durationSeconds(), request.notes());
        return WorkoutSessionResponse.from(useCase.completeSession(user.id(), sessionId, data));
    }

    @PutMapping("/sessions/{sessionId}/abort")
    public WorkoutSessionResponse abortSession(@AuthenticationPrincipal AuthenticatedUser user,
                                               @PathVariable Long sessionId,
                                               @Valid @RequestBody AbortSessionRequest request) {
        return WorkoutSessionResponse.from(useCase.abortSession(user.id(), sessionId, request.notes()));
    }

    @PostMapping("/sessions/{sessionId}/exercises")
    @ResponseStatus(HttpStatus.CREATED)
    public SessionExerciseResponse logSessionExercise(@AuthenticationPrincipal AuthenticatedUser user,
                                                       @PathVariable Long sessionId,
                                                       @Valid @RequestBody SessionExerciseRequest request) {
        SessionExerciseData data = new SessionExerciseData(request.workoutExerciseId(),
                request.exerciseId(), request.setsDone(), request.weightKg(), request.reps(),
                request.rpe(), request.durationSeconds(), request.distanceMeters(), request.notes());
        return SessionExerciseResponse.from(useCase.logSessionExercise(user.id(), sessionId, data));
    }
}
