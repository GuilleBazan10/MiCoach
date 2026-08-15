package com.micoach.nutrition.domain;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Catálogo de alimentos con macros por 100 g (tabla nutrition_ingredients).
 */
@Getter
public class Ingredient {

    private final Long id;
    private final String name;
    private final String category;
    private final String baseUnit;
    private final BigDecimal caloriesPer100g;
    private final BigDecimal proteinPer100g;
    private final BigDecimal carbsPer100g;
    private final BigDecimal fatPer100g;
    private final BigDecimal fiberPer100g;
    private final boolean aiGenerated;
    private final boolean active;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Ingredient(Long id, String name, String category, String baseUnit,
                       BigDecimal caloriesPer100g, BigDecimal proteinPer100g, BigDecimal carbsPer100g,
                       BigDecimal fatPer100g, BigDecimal fiberPer100g, boolean aiGenerated, boolean active,
                       Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.baseUnit = baseUnit;
        this.caloriesPer100g = caloriesPer100g;
        this.proteinPer100g = proteinPer100g;
        this.carbsPer100g = carbsPer100g;
        this.fatPer100g = fatPer100g;
        this.fiberPer100g = fiberPer100g;
        this.aiGenerated = aiGenerated;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Ingredient restore(Long id, String name, String category, String baseUnit,
                                     BigDecimal caloriesPer100g, BigDecimal proteinPer100g,
                                     BigDecimal carbsPer100g, BigDecimal fatPer100g, BigDecimal fiberPer100g,
                                     boolean aiGenerated, boolean active, Instant createdAt, Instant updatedAt) {
        return new Ingredient(id, name, category, baseUnit, caloriesPer100g, proteinPer100g, carbsPer100g,
                fatPer100g, fiberPer100g, aiGenerated, active, createdAt, updatedAt);
    }
}
