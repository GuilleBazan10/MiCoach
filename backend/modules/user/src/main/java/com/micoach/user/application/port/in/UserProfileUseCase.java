package com.micoach.user.application.port.in;

import com.micoach.user.domain.UserGoal;
import com.micoach.user.domain.UserInjury;
import com.micoach.user.domain.UserMedication;
import com.micoach.user.domain.UserPathology;
import com.micoach.user.domain.UserProfile;

import java.time.LocalDate;
import java.util.List;

/**
 * Puerto de entrada del módulo user (casos de uso del perfil de salud).
 */
public interface UserProfileUseCase {

    UserProfile getOrCreateProfile(Long userId);

    UserProfile updateProfile(Long userId, ProfileUpdate data);

    List<UserGoal> getGoals(Long userId);

    UserGoal addGoal(Long userId, GoalData data);

    void deleteGoal(Long userId, Long goalId);

    List<UserPathology> getPathologies(Long userId);

    UserPathology addPathology(Long userId, PathologyData data);

    void deletePathology(Long userId, Long pathologyId);

    List<UserInjury> getInjuries(Long userId);

    UserInjury addInjury(Long userId, InjuryData data);

    void deleteInjury(Long userId, Long injuryId);

    List<UserMedication> getMedications(Long userId);

    UserMedication addMedication(Long userId, MedicationData data);

    void deleteMedication(Long userId, Long medicationId);

    record ProfileUpdate(String sex, LocalDate birthDate, java.math.BigDecimal heightCm,
                         java.math.BigDecimal weightKg, String activityLevel, String experienceLevel,
                         List<String> equipment, Integer trainingDaysPerWeek, Integer trainingMinutes,
                         String preferredTime, String timezone, Integer tdeeCalories,
                         String dietaryGoal, String notes) {
    }

    record GoalData(String goalType, java.math.BigDecimal targetValue, String targetUnit,
                    LocalDate targetDate, Integer priority) {
    }

    record PathologyData(String pathology, String notes, LocalDate diagnosedAt) {
    }

    record InjuryData(String bodyPart, String injuryType, String status, String notes,
                      LocalDate occurredAt) {
    }

    record MedicationData(String medicationName, String dosage, String schedule, String notes) {
    }
}