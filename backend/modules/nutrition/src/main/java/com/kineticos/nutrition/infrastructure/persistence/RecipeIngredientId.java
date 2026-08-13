package com.kineticos.nutrition.infrastructure.persistence;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Clave compuesta de {@link RecipeIngredientJpa} (recipe_id, ingredient_id).
 */
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredientId implements Serializable {

    private Long recipeId;
    private Long ingredientId;
}
