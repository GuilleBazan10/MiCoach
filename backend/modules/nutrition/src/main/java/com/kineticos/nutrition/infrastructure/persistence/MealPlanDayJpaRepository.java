package com.kineticos.nutrition.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MealPlanDayJpaRepository extends JpaRepository<MealPlanDayJpa, Long> {

    List<MealPlanDayJpa> findByMealPlanIdOrderByPlanDateAsc(Long mealPlanId);

    void deleteByMealPlanId(Long mealPlanId);
}
