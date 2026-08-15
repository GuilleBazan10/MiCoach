package com.micoach.nutrition.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MealPlanMealJpaRepository extends JpaRepository<MealPlanMealJpa, Long> {

    List<MealPlanMealJpa> findByMealPlanDayIdInOrderByOrderIndexAsc(List<Long> mealPlanDayIds);
}
