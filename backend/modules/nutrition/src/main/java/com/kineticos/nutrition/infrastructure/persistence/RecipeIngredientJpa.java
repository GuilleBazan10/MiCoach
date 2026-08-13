package com.kineticos.nutrition.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entidad JPA de la tabla junction {@code nutrition_recipe_ingredients}.
 */
@Entity
@Table(name = "nutrition_recipe_ingredients")
@IdClass(RecipeIngredientId.class)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredientJpa {

    @Id
    @Column(name = "recipe_id")
    private Long recipeId;

    @Id
    @Column(name = "ingredient_id")
    private Long ingredientId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "unit", nullable = false)
    private String unit;

    @Column(name = "order_index", nullable = false)
    private Short orderIndex;
}
