package com.micoach.nutrition.domain;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * Ingrediente que compone una receta, con la cantidad usada (tabla
 * nutrition_recipe_ingredients). Se devuelve embebido en {@link Recipe}, incluye el
 * nombre del ingrediente para no forzar al caller a resolverlo aparte.
 */
@Getter
public class RecipeIngredient {

    private final Long ingredientId;
    private final String ingredientName;
    private final BigDecimal amount;
    private final String unit;
    private final Integer orderIndex;

    private RecipeIngredient(Long ingredientId, String ingredientName, BigDecimal amount, String unit,
                             Integer orderIndex) {
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.amount = amount;
        this.unit = unit;
        this.orderIndex = orderIndex;
    }

    public static RecipeIngredient restore(Long ingredientId, String ingredientName, BigDecimal amount,
                                           String unit, Integer orderIndex) {
        return new RecipeIngredient(ingredientId, ingredientName, amount, unit, orderIndex);
    }
}
