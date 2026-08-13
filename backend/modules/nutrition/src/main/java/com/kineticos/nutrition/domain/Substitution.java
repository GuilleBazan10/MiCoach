package com.kineticos.nutrition.domain;

import lombok.Getter;

import java.time.Instant;

/**
 * Sustitución inteligente de un ingrediente por otro (tabla nutrition_substitutions).
 */
@Getter
public class Substitution {

    private final Long id;
    private final Long recipeId;
    private final Long ingredientId;
    private final Long substituteIngredientId;
    private final String substituteIngredientName;
    private final String reason;
    private final String notes;
    private final Instant createdAt;

    private Substitution(Long id, Long recipeId, Long ingredientId, Long substituteIngredientId,
                         String substituteIngredientName, String reason, String notes, Instant createdAt) {
        this.id = id;
        this.recipeId = recipeId;
        this.ingredientId = ingredientId;
        this.substituteIngredientId = substituteIngredientId;
        this.substituteIngredientName = substituteIngredientName;
        this.reason = reason;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public static Substitution restore(Long id, Long recipeId, Long ingredientId, Long substituteIngredientId,
                                       String substituteIngredientName, String reason, String notes,
                                       Instant createdAt) {
        return new Substitution(id, recipeId, ingredientId, substituteIngredientId, substituteIngredientName,
                reason, notes, createdAt);
    }
}
