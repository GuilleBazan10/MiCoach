package com.micoach.nutrition.domain;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Registro del diario alimentario: qué comió el usuario y cuándo (tabla
 * nutrition_daily_intake).
 */
@Getter
public class DailyIntakeEntry {

    private final Long id;
    private final Long userId;
    private final Long mealPlanMealId;
    private final Long recipeId;
    private final LocalDate foodDate;
    private final String mealType;
    private final BigDecimal amount;
    private final BigDecimal calories;
    private final BigDecimal proteinG;
    private final BigDecimal carbsG;
    private final BigDecimal fatG;
    private final Instant consumedAt;

    private DailyIntakeEntry(Long id, Long userId, Long mealPlanMealId, Long recipeId, LocalDate foodDate,
                             String mealType, BigDecimal amount, BigDecimal calories, BigDecimal proteinG,
                             BigDecimal carbsG, BigDecimal fatG, Instant consumedAt) {
        this.id = id;
        this.userId = userId;
        this.mealPlanMealId = mealPlanMealId;
        this.recipeId = recipeId;
        this.foodDate = foodDate;
        this.mealType = mealType;
        this.amount = amount;
        this.calories = calories;
        this.proteinG = proteinG;
        this.carbsG = carbsG;
        this.fatG = fatG;
        this.consumedAt = consumedAt;
    }

    public static DailyIntakeEntry create(Long userId, Long mealPlanMealId, Long recipeId, LocalDate foodDate,
                                          String mealType, BigDecimal amount, BigDecimal calories,
                                          BigDecimal proteinG, BigDecimal carbsG, BigDecimal fatG) {
        return new DailyIntakeEntry(null, userId, mealPlanMealId, recipeId, foodDate, mealType, amount,
                calories, proteinG, carbsG, fatG, Instant.now());
    }

    public static DailyIntakeEntry restore(Long id, Long userId, Long mealPlanMealId, Long recipeId,
                                           LocalDate foodDate, String mealType, BigDecimal amount,
                                           BigDecimal calories, BigDecimal proteinG, BigDecimal carbsG,
                                           BigDecimal fatG, Instant consumedAt) {
        return new DailyIntakeEntry(id, userId, mealPlanMealId, recipeId, foodDate, mealType, amount, calories,
                proteinG, carbsG, fatG, consumedAt);
    }

    public boolean belongsTo(Long userId) {
        return this.userId.equals(userId);
    }
}
