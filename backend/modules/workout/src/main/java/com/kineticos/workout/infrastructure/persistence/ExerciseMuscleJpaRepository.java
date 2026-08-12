package com.kineticos.workout.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseMuscleJpaRepository extends JpaRepository<ExerciseMuscleJpa, ExerciseMuscleId> {

    List<ExerciseMuscleJpa> findByExerciseId(Long exerciseId);

    List<ExerciseMuscleJpa> findByExerciseIdIn(List<Long> exerciseIds);
}
