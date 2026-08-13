package com.kineticos.nutrition.domain;

import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Lista de compra del usuario (tabla nutrition_shopping_lists), agregado con sus ítems.
 */
@Getter
public class ShoppingList {

    private final Long id;
    private final Long userId;
    private final String name;
    private final LocalDate weekStart;
    private List<ShoppingListItem> items;
    private final Instant createdAt;
    private Instant updatedAt;

    private ShoppingList(Long id, Long userId, String name, LocalDate weekStart, List<ShoppingListItem> items,
                         Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.weekStart = weekStart;
        this.items = items == null ? List.of() : items;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ShoppingList create(Long userId, String name, LocalDate weekStart) {
        Instant now = Instant.now();
        return new ShoppingList(null, userId, name == null ? "Lista de la semana" : name, weekStart, List.of(),
                now, now);
    }

    public static ShoppingList restore(Long id, Long userId, String name, LocalDate weekStart,
                                       List<ShoppingListItem> items, Instant createdAt, Instant updatedAt) {
        return new ShoppingList(id, userId, name, weekStart, items, createdAt, updatedAt);
    }

    public boolean belongsTo(Long userId) {
        return this.userId.equals(userId);
    }

    public void touch() {
        this.updatedAt = Instant.now();
    }
}
