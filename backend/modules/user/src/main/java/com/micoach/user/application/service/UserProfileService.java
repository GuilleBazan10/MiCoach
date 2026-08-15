package com.micoach.user.application.service;

import com.micoach.shared.error.DomainException;
import com.micoach.shared.error.ErrorCode;
import com.micoach.user.application.port.in.UserProfileUseCase;
import com.micoach.user.application.port.out.UserProfileRepository;
import com.micoach.user.domain.UserGoal;
import com.micoach.user.domain.UserInjury;
import com.micoach.user.domain.UserMedication;
import com.micoach.user.domain.UserPathology;
import com.micoach.user.domain.UserProfile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación de casos de uso del perfil de salud. Depende solo del puerto de salida.
 */
@Service
public class UserProfileService implements UserProfileUseCase {

    private final UserProfileRepository repository;

    public UserProfileService(UserProfileRepository repository) {
        this.repository = repository;
    }

    // ------------------------- Perfil -------------------------

    @Override
    @Transactional
    public UserProfile getOrCreateProfile(Long userId) {
        return repository.findByUserId(userId)
                .orElseGet(() -> repository.save(UserProfile.empty(userId)));
    }

    @Override
    @Transactional
    public UserProfile updateProfile(Long userId, ProfileUpdate data) {
        UserProfile profile = repository.findByUserId(userId)
                .orElseThrow(() -> new DomainException(404, ErrorCode.NOT_FOUND,
                        "Perfil no encontrado. Crea tu perfil primero."));
        profile.update(data.sex(), data.birthDate(), data.heightCm(), data.weightKg(),
                data.activityLevel(), data.experienceLevel(), data.equipment(),
                data.trainingDaysPerWeek(), data.trainingMinutes(), data.preferredTime(),
                data.timezone(), data.tdeeCalories(), data.dietaryGoal(), data.notes());
        return repository.save(profile);
    }

    // ------------------------- Objetivos -------------------------

    @Override
    @Transactional(readOnly = true)
    public List<UserGoal> getGoals(Long userId) {
        return repository.findGoals(requireProfile(userId).getId());
    }

    @Override
    @Transactional
    public UserGoal addGoal(Long userId, GoalData data) {
        UserProfile profile = requireProfile(userId);
        return repository.saveGoal(UserGoal.create(profile.getId(), data.goalType(),
                data.targetValue(), data.targetUnit(), data.targetDate(), data.priority()));
    }

    @Override
    @Transactional
    public void deleteGoal(Long userId, Long goalId) {
        requireProfile(userId);
        repository.deleteGoal(goalId);
    }

    // ------------------------- Patologías -------------------------

    @Override
    @Transactional(readOnly = true)
    public List<UserPathology> getPathologies(Long userId) {
        return repository.findPathologies(requireProfile(userId).getId());
    }

    @Override
    @Transactional
    public UserPathology addPathology(Long userId, PathologyData data) {
        UserProfile profile = requireProfile(userId);
        return repository.savePathology(UserPathology.create(profile.getId(), data.pathology(),
                data.notes(), data.diagnosedAt()));
    }

    @Override
    @Transactional
    public void deletePathology(Long userId, Long pathologyId) {
        requireProfile(userId);
        repository.deletePathology(pathologyId);
    }

    // ------------------------- Lesiones -------------------------

    @Override
    @Transactional(readOnly = true)
    public List<UserInjury> getInjuries(Long userId) {
        return repository.findInjuries(requireProfile(userId).getId());
    }

    @Override
    @Transactional
    public UserInjury addInjury(Long userId, InjuryData data) {
        UserProfile profile = requireProfile(userId);
        return repository.saveInjury(UserInjury.create(profile.getId(), data.bodyPart(),
                data.injuryType(), data.status(), data.notes(), data.occurredAt()));
    }

    @Override
    @Transactional
    public void deleteInjury(Long userId, Long injuryId) {
        requireProfile(userId);
        repository.deleteInjury(injuryId);
    }

    // ------------------------- Medicación -------------------------

    @Override
    @Transactional(readOnly = true)
    public List<UserMedication> getMedications(Long userId) {
        return repository.findMedications(requireProfile(userId).getId());
    }

    @Override
    @Transactional
    public UserMedication addMedication(Long userId, MedicationData data) {
        UserProfile profile = requireProfile(userId);
        return repository.saveMedication(UserMedication.create(profile.getId(),
                data.medicationName(), data.dosage(), data.schedule(), data.notes()));
    }

    @Override
    @Transactional
    public void deleteMedication(Long userId, Long medicationId) {
        requireProfile(userId);
        repository.deleteMedication(medicationId);
    }

    private UserProfile requireProfile(Long userId) {
        return repository.findByUserId(userId)
                .orElseThrow(() -> new DomainException(404, ErrorCode.NOT_FOUND,
                        "Perfil no encontrado"));
    }
}