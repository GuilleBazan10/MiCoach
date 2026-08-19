package com.micoach.user.application.service;

import com.micoach.shared.error.DomainException;
import com.micoach.shared.error.ErrorCode;
import com.micoach.shared.event.AuditLogEvent;
import com.micoach.user.application.port.in.UserProfileUseCase;
import com.micoach.user.application.port.out.UserProfileRepository;
import com.micoach.user.domain.UserGoal;
import com.micoach.user.domain.UserInjury;
import com.micoach.user.domain.UserMedication;
import com.micoach.user.domain.UserPathology;
import com.micoach.user.domain.UserProfile;
import com.micoach.user.domain.TdeeCalculator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementación de casos de uso del perfil de salud. Depende solo del puerto de salida.
 */
@Slf4j
@Service
public class UserProfileService implements UserProfileUseCase {

    private final UserProfileRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public UserProfileService(UserProfileRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    // ------------------------- Perfil -------------------------

    @Override
    @Transactional
    public UserProfile getOrCreateProfile(Long userId) {
        log.info("Obteniendo o creando perfil para el usuario ID: {}", userId);
        return repository.findByUserId(userId)
                .orElseGet(() -> {
                    log.info("Perfil no encontrado para el usuario ID: {}. Creando perfil vacío por defecto.", userId);
                    UserProfile emptyProfile = UserProfile.empty(userId);
                    UserProfile saved = repository.save(emptyProfile);
                    eventPublisher.publishEvent(AuditLogEvent.of(userId, "PROFILE_CREATE", "USER_PROFILE", saved.getId()));
                    return saved;
                });
    }

    @Override
    @Transactional
    public UserProfile updateProfile(Long userId, ProfileUpdate data) {
        log.info("Actualizando perfil para el usuario ID: {}", userId);
        UserProfile profile = repository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("Actualización de perfil fallida: perfil no encontrado para el usuario ID: {}", userId);
                    return new DomainException(404, ErrorCode.NOT_FOUND,
                            "Perfil no encontrado. Crea tu perfil primero.");
                });

        // Capturar estado anterior para auditoría
        Map<String, Object> before = new HashMap<>();
        before.put("weightKg", profile.getWeightKg());
        before.put("heightCm", profile.getHeightCm());
        before.put("dietaryGoal", profile.getDietaryGoal());
        before.put("experienceLevel", profile.getExperienceLevel());
        before.put("activityLevel", profile.getActivityLevel());

        Integer tdeeCalories = data.tdeeCalories() != null
                ? data.tdeeCalories()
                : TdeeCalculator.calculate(data.sex(), data.birthDate(), data.heightCm(), data.weightKg(),
                        data.activityLevel(), data.dietaryGoal());
        
        profile.update(data.sex(), data.birthDate(), data.heightCm(), data.weightKg(),
                data.activityLevel(), data.experienceLevel(), data.equipment(),
                data.trainingDaysPerWeek(), data.trainingMinutes(), data.preferredTime(),
                data.timezone(), tdeeCalories, data.dietaryGoal(), data.notes());
        
        UserProfile saved = repository.save(profile);

        // Capturar estado nuevo para auditoría
        Map<String, Object> after = new HashMap<>();
        after.put("weightKg", saved.getWeightKg());
        after.put("heightCm", saved.getHeightCm());
        after.put("dietaryGoal", saved.getDietaryGoal());
        after.put("experienceLevel", saved.getExperienceLevel());
        after.put("activityLevel", saved.getActivityLevel());

        log.info("Perfil actualizado exitosamente para el usuario ID: {} (Perfil ID: {})", userId, saved.getId());
        eventPublisher.publishEvent(AuditLogEvent.of(userId, "PROFILE_UPDATE", "USER_PROFILE", saved.getId(), before, after));

        return saved;
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
        log.info("Añadiendo objetivo de tipo: {} para el usuario ID: {}", data.goalType(), userId);
        
        UserGoal goal = UserGoal.create(profile.getId(), data.goalType(),
                data.targetValue(), data.targetUnit(), data.targetDate(), data.priority());
        UserGoal saved = repository.saveGoal(goal);

        log.info("Objetivo añadido exitosamente con ID: {} para el usuario ID: {}", saved.getId(), userId);
        eventPublisher.publishEvent(AuditLogEvent.of(userId, "GOAL_ADD", "USER_GOAL", saved.getId()));
        return saved;
    }

    @Override
    @Transactional
    public void deleteGoal(Long userId, Long goalId) {
        requireProfile(userId);
        log.info("Eliminando objetivo ID: {} para el usuario ID: {}", goalId, userId);
        repository.deleteGoal(goalId);
        eventPublisher.publishEvent(AuditLogEvent.of(userId, "GOAL_DELETE", "USER_GOAL", goalId));
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
        log.info("Añadiendo patología: {} para el usuario ID: {}", data.pathology(), userId);
        
        UserPathology pathology = UserPathology.create(profile.getId(), data.pathology(),
                data.notes(), data.diagnosedAt());
        UserPathology saved = repository.savePathology(pathology);

        log.info("Patología añadida exitosamente con ID: {} para el usuario ID: {}", saved.getId(), userId);
        eventPublisher.publishEvent(AuditLogEvent.of(userId, "PATHOLOGY_ADD", "USER_PATHOLOGY", saved.getId()));
        return saved;
    }

    @Override
    @Transactional
    public void deletePathology(Long userId, Long pathologyId) {
        requireProfile(userId);
        log.info("Eliminando patología ID: {} para el usuario ID: {}", pathologyId, userId);
        repository.deletePathology(pathologyId);
        eventPublisher.publishEvent(AuditLogEvent.of(userId, "PATHOLOGY_DELETE", "USER_PATHOLOGY", pathologyId));
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
        log.info("Añadiendo lesión en parte del cuerpo: {} para el usuario ID: {}", data.bodyPart(), userId);
        
        UserInjury injury = UserInjury.create(profile.getId(), data.bodyPart(),
                data.injuryType(), data.status(), data.notes(), data.occurredAt());
        UserInjury saved = repository.saveInjury(injury);

        log.info("Lesión añadida exitosamente con ID: {} para el usuario ID: {}", saved.getId(), userId);
        eventPublisher.publishEvent(AuditLogEvent.of(userId, "INJURY_ADD", "USER_INJURY", saved.getId()));
        return saved;
    }

    @Override
    @Transactional
    public void deleteInjury(Long userId, Long injuryId) {
        requireProfile(userId);
        log.info("Eliminando lesión ID: {} para el usuario ID: {}", injuryId, userId);
        repository.deleteInjury(injuryId);
        eventPublisher.publishEvent(AuditLogEvent.of(userId, "INJURY_DELETE", "USER_INJURY", injuryId));
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
        log.info("Añadiendo medicación: {} para el usuario ID: {}", data.medicationName(), userId);
        
        UserMedication medication = UserMedication.create(profile.getId(),
                data.medicationName(), data.dosage(), data.schedule(), data.notes());
        UserMedication saved = repository.saveMedication(medication);

        log.info("Medicación añadida exitosamente con ID: {} para el usuario ID: {}", saved.getId(), userId);
        eventPublisher.publishEvent(AuditLogEvent.of(userId, "MEDICATION_ADD", "USER_MEDICATION", saved.getId()));
        return saved;
    }

    @Override
    @Transactional
    public void deleteMedication(Long userId, Long medicationId) {
        requireProfile(userId);
        log.info("Eliminando medicación ID: {} para el usuario ID: {}", medicationId, userId);
        repository.deleteMedication(medicationId);
        eventPublisher.publishEvent(AuditLogEvent.of(userId, "MEDICATION_DELETE", "USER_MEDICATION", medicationId));
    }

    private UserProfile requireProfile(Long userId) {
        return repository.findByUserId(userId)
                .orElseThrow(() -> new DomainException(404, ErrorCode.NOT_FOUND,
                        "Perfil no encontrado"));
    }
}