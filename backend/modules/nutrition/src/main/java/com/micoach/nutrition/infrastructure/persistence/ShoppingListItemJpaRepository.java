package com.micoach.nutrition.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShoppingListItemJpaRepository extends JpaRepository<ShoppingListItemJpa, Long> {

    List<ShoppingListItemJpa> findByShoppingListId(Long shoppingListId);
}
