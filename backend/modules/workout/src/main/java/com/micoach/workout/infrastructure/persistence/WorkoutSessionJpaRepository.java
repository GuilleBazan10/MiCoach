package com.micoach.workout.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutSessionJpaRepository extends JpaRepository<WorkoutSessionJpa, Long> {

    List<WorkoutSessionJpa> findByUserIdOrderByStartedAtDesc(Long userId);
}
