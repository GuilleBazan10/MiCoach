package com.kineticos.user.presentation;

import com.kineticos.shared.security.AuthenticatedUser;
import com.kineticos.user.application.port.in.UserProfileUseCase;
import com.kineticos.user.application.port.in.UserProfileUseCase.GoalData;
import com.kineticos.user.application.port.in.UserProfileUseCase.InjuryData;
import com.kineticos.user.application.port.in.UserProfileUseCase.MedicationData;
import com.kineticos.user.application.port.in.UserProfileUseCase.PathologyData;
import com.kineticos.user.application.port.in.UserProfileUseCase.ProfileUpdate;
import com.kineticos.user.presentation.UserDtos.GoalRequest;
import com.kineticos.user.presentation.UserDtos.GoalResponse;
import com.kineticos.user.presentation.UserDtos.InjuryRequest;
import com.kineticos.user.presentation.UserDtos.InjuryResponse;
import com.kineticos.user.presentation.UserDtos.MedicationRequest;
import com.kineticos.user.presentation.UserDtos.MedicationResponse;
import com.kineticos.user.presentation.UserDtos.PathologyRequest;
import com.kineticos.user.presentation.UserDtos.PathologyResponse;
import com.kineticos.user.presentation.UserDtos.ProfileResponse;
import com.kineticos.user.presentation.UserDtos.ProfileUpdateRequest;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Contractos REST del módulo user (base path /api/v1/users/me).
 * Todos requieren JWT (configurado en app/security).
 */
@RestController
@RequestMapping("/api/v1/users/me")
public class UserController {

    private final UserProfileUseCase useCase;

    public UserController(UserProfileUseCase useCase) {
        this.useCase = useCase;
    }

    // ------------------------- Perfil -------------------------

    @GetMapping("/profile")
    public ProfileResponse getOrCreateProfile(@AuthenticationPrincipal AuthenticatedUser user) {
        return ProfileResponse.from(useCase.getOrCreateProfile(user.id()));
    }

    @PutMapping("/profile")
    public ProfileResponse updateProfile(@AuthenticationPrincipal AuthenticatedUser user,
                                         @Valid @RequestBody ProfileUpdateRequest request) {
        ProfileUpdate data = new ProfileUpdate(request.sex(), request.birthDate(),
                request.heightCm(), request.weightKg(), request.activityLevel(),
                request.experienceLevel(), request.equipment(), request.trainingDaysPerWeek(),
                request.trainingMinutes(), request.preferredTime(), request.timezone(),
                request.tdeeCalories(), request.dietaryGoal(), request.notes());
        return ProfileResponse.from(useCase.updateProfile(user.id(), data));
    }

    // ------------------------- Objetivos -------------------------

    @GetMapping("/goals")
    public List<GoalResponse> getGoals(@AuthenticationPrincipal AuthenticatedUser user) {
        return useCase.getGoals(user.id()).stream().map(GoalResponse::from).toList();
    }

    @PostMapping("/goals")
    @ResponseStatus(HttpStatus.CREATED)
    public GoalResponse addGoal(@AuthenticationPrincipal AuthenticatedUser user,
                                @Valid @RequestBody GoalRequest request) {
        GoalData data = new GoalData(request.goalType(), request.targetValue(),
                request.targetUnit(), request.targetDate(), request.priority());
        return GoalResponse.from(useCase.addGoal(user.id(), data));
    }

    @DeleteMapping("/goals/{goalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGoal(@AuthenticationPrincipal AuthenticatedUser user,
                           @PathVariable Long goalId) {
        useCase.deleteGoal(user.id(), goalId);
    }

    // ------------------------- Patologías -------------------------

    @GetMapping("/pathologies")
    public List<PathologyResponse> getPathologies(@AuthenticationPrincipal AuthenticatedUser user) {
        return useCase.getPathologies(user.id()).stream().map(PathologyResponse::from).toList();
    }

    @PostMapping("/pathologies")
    @ResponseStatus(HttpStatus.CREATED)
    public PathologyResponse addPathology(@AuthenticationPrincipal AuthenticatedUser user,
                                          @Valid @RequestBody PathologyRequest request) {
        PathologyData data = new PathologyData(request.pathology(), request.notes(),
                request.diagnosedAt());
        return PathologyResponse.from(useCase.addPathology(user.id(), data));
    }

    @DeleteMapping("/pathologies/{pathologyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePathology(@AuthenticationPrincipal AuthenticatedUser user,
                                @PathVariable Long pathologyId) {
        useCase.deletePathology(user.id(), pathologyId);
    }

    // ------------------------- Lesiones -------------------------

    @GetMapping("/injuries")
    public List<InjuryResponse> getInjuries(@AuthenticationPrincipal AuthenticatedUser user) {
        return useCase.getInjuries(user.id()).stream().map(InjuryResponse::from).toList();
    }

    @PostMapping("/injuries")
    @ResponseStatus(HttpStatus.CREATED)
    public InjuryResponse addInjury(@AuthenticationPrincipal AuthenticatedUser user,
                                    @Valid @RequestBody InjuryRequest request) {
        InjuryData data = new InjuryData(request.bodyPart(), request.injuryType(),
                request.status(), request.notes(), request.occurredAt());
        return InjuryResponse.from(useCase.addInjury(user.id(), data));
    }

    @DeleteMapping("/injuries/{injuryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInjury(@AuthenticationPrincipal AuthenticatedUser user,
                             @PathVariable Long injuryId) {
        useCase.deleteInjury(user.id(), injuryId);
    }

    // ------------------------- Medicación -------------------------

    @GetMapping("/medications")
    public List<MedicationResponse> getMedications(@AuthenticationPrincipal AuthenticatedUser user) {
        return useCase.getMedications(user.id()).stream().map(MedicationResponse::from).toList();
    }

    @PostMapping("/medications")
    @ResponseStatus(HttpStatus.CREATED)
    public MedicationResponse addMedication(@AuthenticationPrincipal AuthenticatedUser user,
                                            @Valid @RequestBody MedicationRequest request) {
        MedicationData data = new MedicationData(request.medicationName(), request.dosage(),
                request.schedule(), request.notes());
        return MedicationResponse.from(useCase.addMedication(user.id(), data));
    }

    @DeleteMapping("/medications/{medicationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMedication(@AuthenticationPrincipal AuthenticatedUser user,
                                 @PathVariable Long medicationId) {
        useCase.deleteMedication(user.id(), medicationId);
    }
}