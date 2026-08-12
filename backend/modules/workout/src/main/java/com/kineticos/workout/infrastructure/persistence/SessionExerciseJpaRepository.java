package com.kineticos.workout.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionExerciseJpaRepository extends JpaRepository<SessionExerciseJpa, Long> {

    List<SessionExerciseJpa> findBySessionIdOrderByIdAsc(Long sessionId);
}
