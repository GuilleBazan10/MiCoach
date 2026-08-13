package com.kineticos.nutrition.domain;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Catálogo de recetas con macros por porción (tabla nutrition_recipes).
 */
@Getter
public class Recipe {

    private final Long id;
    private final String name;
    private final String description;
    private final String mealCategory;
    private final String difficulty;
    private final Integer servings;
    private final Integer prepTimeMin;
    private final Integer cookTimeMin;
    private final BigDecimal caloriesPerServing;
    private final BigDecimal proteinPerServing;
    private final BigDecimal carbsPerServing;
    private final BigDecimal fatPerServing;
    private final BigDecimal fiberPerServing;
    private final String instructions;
    private final String imageUrl;
    private final boolean aiGenerated;
    private final boolean active;
    private final List<RecipeIngredient> ingredients;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Recipe(Long id, String name, String description, String mealCategory, String difficulty,
                   Integer servings, Integer prepTimeMin, Integer cookTimeMin, BigDecimal caloriesPerServing,
                   BigDecimal proteinPerServing, BigDecimal carbsPerServing, BigDecimal fatPerServing,
                   BigDecimal fiberPerServing, String instructions, String imageUrl, boolean aiGenerated,
                   boolean active, List<RecipeIngredient> ingredients, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.mealCategory = mealCategory;
        this.difficulty = difficulty;
        this.servings = servings;
        this.prepTimeMin = prepTimeMin;
        this.cookTimeMin = cookTimeMin;
        this.caloriesPerServing = caloriesPerServing;
        this.proteinPerServing = proteinPerServing;
        this.carbsPerServing = carbsPerServing;
        this.fatPerServing = fatPerServing;
        this.fiberPerServing = fiberPerServing;
        this.instructions = instructions;
        this.imageUrl = imageUrl;
        this.aiGenerated = aiGenerated;
        this.active = active;
        this.ingredients = ingredients == null ? List.of() : ingredients;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Recipe restore(Long id, String name, String description, String mealCategory,
                                 String difficulty, Integer servings, Integer prepTimeMin, Integer cookTimeMin,
                                 BigDecimal caloriesPerServing, BigDecimal proteinPerServing,
                                 BigDecimal carbsPerServing, BigDecimal fatPerServing, BigDecimal fiberPerServing,
                                 String instructions, String imageUrl, boolean aiGenerated, boolean active,
                                 List<RecipeIngredient> ingredients, Instant createdAt, Instant updatedAt) {
        return new Recipe(id, name, description, mealCategory, difficulty, servings, prepTimeMin, cookTimeMin,
                caloriesPerServing, proteinPerServing, carbsPerServing, fatPerServing, fiberPerServing,
                instructions, imageUrl, aiGenerated, active, ingredients, createdAt, updatedAt);
    }
}
