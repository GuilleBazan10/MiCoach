package com.kineticos.nutrition.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IngredientJpaRepository extends JpaRepository<IngredientJpa, Long> {

    List<IngredientJpa> findByActiveTrue();
}
