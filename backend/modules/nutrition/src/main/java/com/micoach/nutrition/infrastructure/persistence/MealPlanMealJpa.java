package com.micoach.nutrition.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entidad JPA de la tabla {@code nutrition_meal_plan_meals}.
 */
@Entity
@Table(name = "nutrition_meal_plan_meals")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanMealJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "meal_plan_day_id", nullable = false)
    private Long mealPlanDayId;

    @Column(name = "recipe_id")
    private Long recipeId;

    @Column(name = "meal_type", nullable = false)
    private String mealType;

    @Column(name = "order_index", nullable = false)
    private Short orderIndex;

    @Column(name = "servings", nullable = false)
    private BigDecimal servings;

    @Column(name = "notes")
    private String notes;
}
