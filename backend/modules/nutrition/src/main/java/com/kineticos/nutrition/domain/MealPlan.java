package com.kineticos.nutrition.domain;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Plan de alimentación del usuario (tabla nutrition_meal_plans), agregado con sus días
 * y comidas.
 */
@Getter
public class MealPlan {

    private Long id;
    private final Long userId;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer targetCalories;
    private BigDecimal targetProteinG;
    private BigDecimal targetCarbsG;
    private BigDecimal targetFatG;
    private final boolean aiGenerated;
    private String status;
    private List<MealPlanDay> days;
    private final Instant createdAt;
    private Instant updatedAt;

    private MealPlan(Long id, Long userId, String name, String description, LocalDate startDate,
                     LocalDate endDate, Integer targetCalories, BigDecimal targetProteinG,
                     BigDecimal targetCarbsG, BigDecimal targetFatG, boolean aiGenerated, String status,
                     List<MealPlanDay> days, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.targetCalories = targetCalories;
        this.targetProteinG = targetProteinG;
        this.targetCarbsG = targetCarbsG;
        this.targetFatG = targetFatG;
        this.aiGenerated = aiGenerated;
        this.status = status;
        this.days = days == null ? List.of() : days;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static MealPlan create(Long userId, String name, String description, LocalDate startDate,
                                  LocalDate endDate, Integer targetCalories, BigDecimal targetProteinG,
                                  BigDecimal targetCarbsG, BigDecimal targetFatG, List<MealPlanDay> days) {
        Instant now = Instant.now();
        return new MealPlan(null, userId, name, description, startDate, endDate, targetCalories,
                targetProteinG, targetCarbsG, targetFatG, false, "active", days, now, now);
    }

    public static MealPlan restore(Long id, Long userId, String name, String description, LocalDate startDate,
                                   LocalDate endDate, Integer targetCalories, BigDecimal targetProteinG,
                                   BigDecimal targetCarbsG, BigDecimal targetFatG, boolean aiGenerated,
                                   String status, List<MealPlanDay> days, Instant createdAt, Instant updatedAt) {
        return new MealPlan(id, userId, name, description, startDate, endDate, targetCalories, targetProteinG,
                targetCarbsG, targetFatG, aiGenerated, status, days, createdAt, updatedAt);
    }

    public void update(String name, String description, LocalDate startDate, LocalDate endDate,
                       Integer targetCalories, BigDecimal targetProteinG, BigDecimal targetCarbsG,
                       BigDecimal targetFatG, List<MealPlanDay> days) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.targetCalories = targetCalories;
        this.targetProteinG = targetProteinG;
        this.targetCarbsG = targetCarbsG;
        this.targetFatG = targetFatG;
        this.days = days == null ? List.of() : days;
        this.updatedAt = Instant.now();
    }

    public boolean belongsTo(Long userId) {
        return this.userId.equals(userId);
    }
}
