package com.kineticos.workout.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutJpaRepository extends JpaRepository<WorkoutJpa, Long> {

    List<WorkoutJpa> findByUserId(Long userId);

    List<WorkoutJpa> findByTemplateTrue();
}
