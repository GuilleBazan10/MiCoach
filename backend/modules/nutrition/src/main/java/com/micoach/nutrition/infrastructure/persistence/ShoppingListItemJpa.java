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
 * Entidad JPA de la tabla {@code nutrition_shopping_list_items}.
 */
@Entity
@Table(name = "nutrition_shopping_list_items")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingListItemJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "shopping_list_id", nullable = false)
    private Long shoppingListId;

    @Column(name = "ingredient_id")
    private Long ingredientId;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "unit")
    private String unit;

    @Column(name = "category")
    private String category;

    @Column(name = "is_checked", nullable = false)
    private boolean checked;
}
