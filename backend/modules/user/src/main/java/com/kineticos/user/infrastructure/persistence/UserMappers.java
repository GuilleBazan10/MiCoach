package com.kineticos.user.infrastructure.persistence;

import com.kineticos.user.domain.UserGoal;
import com.kineticos.user.domain.UserInjury;
import com.kineticos.user.domain.UserMedication;
import com.kineticos.user.domain.UserPathology;
import com.kineticos.user.domain.UserProfile;

final class ProfileMapper {

    private ProfileMapper() {
    }

    static UserProfile toDomain(UserProfileJpa jpa) {
        return UserProfile.restore(jpa.getId(), jpa.getUserId(), jpa.getSex(), jpa.getBirthDate(),
                jpa.getHeightCm(), jpa.getWeightKg(), jpa.getActivityLevel(),
                jpa.getExperienceLevel(), jpa.getEquipment(),
                intValue(jpa.getTrainingDaysPerWeek()), intValue(jpa.getTrainingMinutes()),
                jpa.getPreferredTime(), jpa.getTimezone(), jpa.getTdeeCalories(),
                jpa.getDietaryGoal(), jpa.getNotes(), jpa.getCreatedAt(), jpa.getUpdatedAt());
    }

    static UserProfileJpa toJpa(UserProfile domain) {
        return UserProfileJpa.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .sex(domain.getSex())
                .birthDate(domain.getBirthDate())
                .heightCm(domain.getHeightCm())
                .weightKg(domain.getWeightKg())
                .activityLevel(domain.getActivityLevel())
                .experienceLevel(domain.getExperienceLevel())
                .equipment(domain.getEquipment())
                .trainingDaysPerWeek(shortValue(domain.getTrainingDaysPerWeek()))
                .trainingMinutes(shortValue(domain.getTrainingMinutes()))
                .preferredTime(domain.getPreferredTime())
                .timezone(domain.getTimezone())
                .tdeeCalories(domain.getTdeeCalories())
                .dietaryGoal(domain.getDietaryGoal())
                .notes(domain.getNotes())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    static Integer intValue(Short v) {
        return v == null ? null : v.intValue();
    }

    static Short shortValue(Integer v) {
        return v == null ? null : v.shortValue();
    }
}

final class GoalMapper {

    private GoalMapper() {
    }

    static UserGoal toDomain(UserGoalJpa jpa) {
        return UserGoal.restore(jpa.getId(), jpa.getProfileId(), jpa.getGoalType(),
                jpa.getTargetValue(), jpa.getTargetUnit(), jpa.getTargetDate(),
                ProfileMapper.intValue(jpa.getPriority()), jpa.isActive(),
                jpa.getCreatedAt(), jpa.getUpdatedAt());
    }

    static UserGoalJpa toJpa(UserGoal domain) {
        return UserGoalJpa.builder()
                .id(domain.getId())
                .profileId(domain.getProfileId())
                .goalType(domain.getGoalType())
                .targetValue(domain.getTargetValue())
                .targetUnit(domain.getTargetUnit())
                .targetDate(domain.getTargetDate())
                .priority(ProfileMapper.shortValue(domain.getPriority()))
                .active(domain.isActive())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}

final class PathologyMapper {

    private PathologyMapper() {
    }

    static UserPathology toDomain(UserPathologyJpa jpa) {
        return UserPathology.restore(jpa.getId(), jpa.getProfileId(), jpa.getPathology(),
                jpa.getNotes(), jpa.getDiagnosedAt(), jpa.getCreatedAt());
    }

    static UserPathologyJpa toJpa(UserPathology domain) {
        return UserPathologyJpa.builder()
                .id(domain.getId())
                .profileId(domain.getProfileId())
                .pathology(domain.getPathology())
                .notes(domain.getNotes())
                .diagnosedAt(domain.getDiagnosedAt())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}

final class InjuryMapper {

    private InjuryMapper() {
    }

    static UserInjury toDomain(UserInjuryJpa jpa) {
        return UserInjury.restore(jpa.getId(), jpa.getProfileId(), jpa.getBodyPart(),
                jpa.getInjuryType(), jpa.getStatus(), jpa.getNotes(), jpa.getOccurredAt(),
                jpa.getCreatedAt());
    }

    static UserInjuryJpa toJpa(UserInjury domain) {
        return UserInjuryJpa.builder()
                .id(domain.getId())
                .profileId(domain.getProfileId())
                .bodyPart(domain.getBodyPart())
                .injuryType(domain.getInjuryType())
                .status(domain.getStatus())
                .notes(domain.getNotes())
                .occurredAt(domain.getOccurredAt())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}

final class MedicationMapper {

    private MedicationMapper() {
    }

    static UserMedication toDomain(UserMedicationJpa jpa) {
        return UserMedication.restore(jpa.getId(), jpa.getProfileId(), jpa.getMedicationName(),
                jpa.getDosage(), jpa.getSchedule(), jpa.getNotes(), jpa.isActive(),
                jpa.getCreatedAt(), jpa.getUpdatedAt());
    }

    static UserMedicationJpa toJpa(UserMedication domain) {
        return UserMedicationJpa.builder()
                .id(domain.getId())
                .profileId(domain.getProfileId())
                .medicationName(domain.getMedicationName())
                .dosage(domain.getDosage())
                .schedule(domain.getSchedule())
                .notes(domain.getNotes())
                .active(domain.isActive())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}