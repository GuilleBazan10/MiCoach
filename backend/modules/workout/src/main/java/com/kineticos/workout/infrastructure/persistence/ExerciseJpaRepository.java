package com.kineticos.workout.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseJpaRepository extends JpaRepository<ExerciseJpa, Long> {

    List<ExerciseJpa> findByActiveTrue();
}
