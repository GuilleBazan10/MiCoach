package com.kineticos.workout.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutDayJpaRepository extends JpaRepository<WorkoutDayJpa, Long> {

    List<WorkoutDayJpa> findByWorkoutIdOrderByDayIndexAsc(Long workoutId);

    void deleteByWorkoutId(Long workoutId);
}
