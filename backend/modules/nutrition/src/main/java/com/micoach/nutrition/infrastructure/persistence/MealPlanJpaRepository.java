package com.micoach.nutrition.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MealPlanJpaRepository extends JpaRepository<MealPlanJpa, Long> {

    List<MealPlanJpa> findByUserId(Long userId);
}
