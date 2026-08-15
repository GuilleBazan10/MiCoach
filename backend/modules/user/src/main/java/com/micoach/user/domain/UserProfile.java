package com.micoach.user.domain;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;

/**
 * Perfil de salud del usuario (tabla user_profiles). 1:1 con auth_users.
 * Valores aquí son iniciales/actuales; el histórico vive en progress_entries.
 */
@Getter
public class UserProfile {

    private Long id;
    private final Long userId;
    private String sex;
    private LocalDate birthDate;
    private BigDecimal heightCm;
    private BigDecimal weightKg;
    private String activityLevel;
    private String experienceLevel;
    private List<String> equipment;
    private Integer trainingDaysPerWeek;
    private Integer trainingMinutes;
    private String preferredTime;
    private String timezone;
    private Integer tdeeCalories;
    private String dietaryGoal;
    private String notes;
    private final Instant createdAt;
    private Instant updatedAt;

    private UserProfile(Long id, Long userId, String sex, LocalDate birthDate,
                        BigDecimal heightCm, BigDecimal weightKg, String activityLevel,
                        String experienceLevel, List<String> equipment, Integer trainingDaysPerWeek,
                        Integer trainingMinutes, String preferredTime, String timezone,
                        Integer tdeeCalories, String dietaryGoal, String notes,
                        Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.sex = sex;
        this.birthDate = birthDate;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.activityLevel = activityLevel;
        this.experienceLevel = experienceLevel;
        this.equipment = equipment;
        this.trainingDaysPerWeek = trainingDaysPerWeek;
        this.trainingMinutes = trainingMinutes;
        this.preferredTime = preferredTime;
        this.timezone = timezone;
        this.tdeeCalories = tdeeCalories;
        this.dietaryGoal = dietaryGoal;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UserProfile empty(Long userId) {
        Instant now = Instant.now();
        return new UserProfile(null, userId, null, null, null, null, null, null,
                List.of(), null, null, null, null, null, null, null, now, now);
    }

    public static UserProfile restore(Long id, Long userId, String sex, LocalDate birthDate,
                                      BigDecimal heightCm, BigDecimal weightKg, String activityLevel,
                                      String experienceLevel, List<String> equipment,
                                      Integer trainingDaysPerWeek, Integer trainingMinutes,
                                      String preferredTime, String timezone, Integer tdeeCalories,
                                      String dietaryGoal, String notes,
                                      Instant createdAt, Instant updatedAt) {
        return new UserProfile(id, userId, sex, birthDate, heightCm, weightKg, activityLevel,
                experienceLevel, equipment == null ? List.of() : equipment, trainingDaysPerWeek,
                trainingMinutes, preferredTime, timezone, tdeeCalories, dietaryGoal, notes,
                createdAt, updatedAt);
    }

    public void update(String sex, LocalDate birthDate, BigDecimal heightCm, BigDecimal weightKg,
                       String activityLevel, String experienceLevel, List<String> equipment,
                       Integer trainingDaysPerWeek, Integer trainingMinutes, String preferredTime,
                       String timezone, Integer tdeeCalories, String dietaryGoal, String notes) {
        this.sex = sex;
        this.birthDate = birthDate;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.activityLevel = activityLevel;
        this.experienceLevel = experienceLevel;
        this.equipment = equipment;
        this.trainingDaysPerWeek = trainingDaysPerWeek;
        this.trainingMinutes = trainingMinutes;
        this.preferredTime = preferredTime;
        this.timezone = timezone;
        this.tdeeCalories = tdeeCalories;
        this.dietaryGoal = dietaryGoal;
        this.notes = notes;
        this.updatedAt = Instant.now();
    }
}