package com.kineticos.workout.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlannedExerciseJpaRepository extends JpaRepository<PlannedExerciseJpa, Long> {

    List<PlannedExerciseJpa> findByWorkoutDayIdInOrderByOrderIndexAsc(List<Long> workoutDayIds);
}
