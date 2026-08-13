package com.kineticos.nutrition.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeIngredientJpaRepository extends JpaRepository<RecipeIngredientJpa, RecipeIngredientId> {

    List<RecipeIngredientJpa> findByRecipeIdInOrderByOrderIndexAsc(List<Long> recipeIds);
}
