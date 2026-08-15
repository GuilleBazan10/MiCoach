package com.micoach.nutrition.domain;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * Comida dentro de un día de plan (tabla nutrition_meal_plan_meals).
 */
@Getter
public class MealPlanMeal {

    private final Long id;
    private final Long recipeId;
    private final String mealType;
    private final Integer orderIndex;
    private final BigDecimal servings;
    private final String notes;

    private MealPlanMeal(Long id, Long recipeId, String mealType, Integer orderIndex, BigDecimal servings,
                         String notes) {
        this.id = id;
        this.recipeId = recipeId;
        this.mealType = mealType;
        this.orderIndex = orderIndex;
        this.servings = servings;
        this.notes = notes;
    }

    public static MealPlanMeal create(Long recipeId, String mealType, Integer orderIndex, BigDecimal servings,
                                      String notes) {
        return new MealPlanMeal(null, recipeId, mealType, orderIndex,
                servings == null ? BigDecimal.ONE : servings, notes);
    }

    public static MealPlanMeal restore(Long id, Long recipeId, String mealType, Integer orderIndex,
                                       BigDecimal servings, String notes) {
        return new MealPlanMeal(id, recipeId, mealType, orderIndex, servings, notes);
    }
}
