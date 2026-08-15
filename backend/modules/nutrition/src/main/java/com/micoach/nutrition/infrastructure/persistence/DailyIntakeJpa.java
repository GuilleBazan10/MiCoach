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
import java.time.Instant;
import java.time.LocalDate;

/**
 * Entidad JPA de la tabla {@code nutrition_daily_intake} (diario alimentario).
 */
@Entity
@Table(name = "nutrition_daily_intake")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyIntakeJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "meal_plan_meal_id")
    private Long mealPlanMealId;

    @Column(name = "recipe_id")
    private Long recipeId;

    @Column(name = "food_date", nullable = false)
    private LocalDate foodDate;

    @Column(name = "meal_type", nullable = false)
    private String mealType;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "calories")
    private BigDecimal calories;

    @Column(name = "protein_g")
    private BigDecimal proteinG;

    @Column(name = "carbs_g")
    private BigDecimal carbsG;

    @Column(name = "fat_g")
    private BigDecimal fatG;

    @Column(name = "consumed_at", nullable = false)
    private Instant consumedAt;
}
