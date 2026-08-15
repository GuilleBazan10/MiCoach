package com.micoach.nutrition.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeJpaRepository extends JpaRepository<RecipeJpa, Long> {

    List<RecipeJpa> findByActiveTrue();
}
