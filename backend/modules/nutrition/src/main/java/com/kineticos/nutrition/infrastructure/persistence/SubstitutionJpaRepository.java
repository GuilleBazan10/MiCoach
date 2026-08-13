package com.kineticos.nutrition.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubstitutionJpaRepository extends JpaRepository<SubstitutionJpa, Long> {

    List<SubstitutionJpa> findByIngredientId(Long ingredientId);
}
