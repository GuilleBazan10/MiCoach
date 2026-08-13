package com.kineticos.nutrition.domain;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * Ítem de una lista de compra (tabla nutrition_shopping_list_items). {@code ingredientId}
 * nulo representa un ítem libre (no referenciado al catálogo).
 */
@Getter
public class ShoppingListItem {

    private final Long id;
    private final Long ingredientId;
    private final String itemName;
    private final BigDecimal amount;
    private final String unit;
    private final String category;
    private boolean checked;

    private ShoppingListItem(Long id, Long ingredientId, String itemName, BigDecimal amount, String unit,
                             String category, boolean checked) {
        this.id = id;
        this.ingredientId = ingredientId;
        this.itemName = itemName;
        this.amount = amount;
        this.unit = unit;
        this.category = category;
        this.checked = checked;
    }

    public static ShoppingListItem create(Long ingredientId, String itemName, BigDecimal amount, String unit,
                                          String category) {
        return new ShoppingListItem(null, ingredientId, itemName, amount, unit, category, false);
    }

    public static ShoppingListItem restore(Long id, Long ingredientId, String itemName, BigDecimal amount,
                                           String unit, String category, boolean checked) {
        return new ShoppingListItem(id, ingredientId, itemName, amount, unit, category, checked);
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
