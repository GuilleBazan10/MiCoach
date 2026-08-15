package com.micoach.nutrition.domain;

import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

/**
 * Día calendario de un plan de alimentación (tabla nutrition_meal_plan_days).
 */
@Getter
public class MealPlanDay {

    private final Long id;
    private final LocalDate planDate;
    private final List<MealPlanMeal> meals;

    private MealPlanDay(Long id, LocalDate planDate, List<MealPlanMeal> meals) {
        this.id = id;
        this.planDate = planDate;
        this.meals = meals == null ? List.of() : meals;
    }

    public static MealPlanDay create(LocalDate planDate, List<MealPlanMeal> meals) {
        return new MealPlanDay(null, planDate, meals);
    }

    public static MealPlanDay restore(Long id, LocalDate planDate, List<MealPlanMeal> meals) {
        return new MealPlanDay(id, planDate, meals);
    }
}
