package com.micoach.nutrition.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShoppingListJpaRepository extends JpaRepository<ShoppingListJpa, Long> {

    List<ShoppingListJpa> findByUserId(Long userId);
}
