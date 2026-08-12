package com.kineticos.user.presentation;

import com.kineticos.user.domain.UserGoal;
import com.kineticos.user.domain.UserInjury;
import com.kineticos.user.domain.UserMedication;
import com.kineticos.user.domain.UserPathology;
import com.kineticos.user.domain.UserProfile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTOs del módulo user. Cada verbose class es un contrato de entrada/salida.
 */
public final class UserDtos {

    private UserDtos() {
    }

    // ------------------------- Perfil -------------------------

    public record ProfileResponse(Long id, String sex, LocalDate birthDate, BigDecimal heightCm,
                                  BigDecimal weightKg, String activityLevel, String experienceLevel,
                                  List<String> equipment, Integer trainingDaysPerWeek,
                                  Integer trainingMinutes, String preferredTime, String timezone,
                                  Integer tdeeCalories, String dietaryGoal, String notes) {

        static ProfileResponse from(UserProfile p) {
            return new ProfileResponse(p.getId(), p.getSex(), p.getBirthDate(), p.getHeightCm(),
                    p.getWeightKg(), p.getActivityLevel(), p.getExperienceLevel(),
                    p.getEquipment(), p.getTrainingDaysPerWeek(), p.getTrainingMinutes(),
                    p.getPreferredTime(), p.getTimezone(), p.getTdeeCalories(), p.getDietaryGoal(),
                    p.getNotes());
        }
    }

    public record ProfileUpdateRequest(String sex, LocalDate birthDate, BigDecimal heightCm,
                                       BigDecimal weightKg,
                                       @Size(max = 20) String activityLevel,
                                       @Size(max = 20) String experienceLevel,
                                       List<String> equipment,
                                       @jakarta.validation.constraints.Min(1)
                                       @jakarta.validation.constraints.Max(7)
                                       Integer trainingDaysPerWeek,
                                       @jakarta.validation.constraints.Positive
                                       Integer trainingMinutes,
                                       @Size(max = 20) String preferredTime,
                                       @Size(max = 50) String timezone,
                                       @jakarta.validation.constraints.Positive
                                       Integer tdeeCalories,
                                       @Size(max = 20) String dietaryGoal,
                                       @Size(max = 1000) String notes) {
    }

    // ------------------------- Objetivos -------------------------

    public record GoalResponse(Long id, String goalType, BigDecimal targetValue, String targetUnit,
                               LocalDate targetDate, Integer priority, boolean active) {

        static GoalResponse from(UserGoal g) {
            return new GoalResponse(g.getId(), g.getGoalType(), g.getTargetValue(),
                    g.getTargetUnit(), g.getTargetDate(), g.getPriority(), g.isActive());
        }
    }

    public record GoalRequest(@NotBlank String goalType, BigDecimal targetValue,
                              String targetUnit, LocalDate targetDate,
                              @jakarta.validation.constraints.Min(1) Integer priority) {
    }

    // ------------------------- Patologías -------------------------

    public record PathologyResponse(Long id, String pathology, String notes, LocalDate diagnosedAt) {

        static PathologyResponse from(UserPathology p) {
            return new PathologyResponse(p.getId(), p.getPathology(), p.getNotes(),
                    p.getDiagnosedAt());
        }
    }

    public record PathologyRequest(@NotBlank @Size(max = 150) String pathology,
                                   @Size(max = 500) String notes, LocalDate diagnosedAt) {
    }

    // ------------------------- Lesiones -------------------------

    public record InjuryResponse(Long id, String bodyPart, String injuryType, String status,
                                 String notes, LocalDate occurredAt) {

        static InjuryResponse from(UserInjury i) {
            return new InjuryResponse(i.getId(), i.getBodyPart(), i.getInjuryType(), i.getStatus(),
                    i.getNotes(), i.getOccurredAt());
        }
    }

    public record InjuryRequest(@NotBlank @Size(max = 100) String bodyPart,
                                @NotBlank @Size(max = 150) String injuryType,
                                @Size(max = 20) String status,
                                @Size(max = 500) String notes, LocalDate occurredAt) {
    }

    // ------------------------- Medicación -------------------------

    public record MedicationResponse(Long id, String medicationName, String dosage, String schedule,
                                     String notes, boolean active) {

        static MedicationResponse from(UserMedication m) {
            return new MedicationResponse(m.getId(), m.getMedicationName(), m.getDosage(),
                    m.getSchedule(), m.getNotes(), m.isActive());
        }
    }

    public record MedicationRequest(@NotBlank @Size(max = 150) String medicationName,
                                    @Size(max = 100) String dosage,
                                    @Size(max = 200) String schedule,
                                    @Size(max = 500) String notes) {
    }
}