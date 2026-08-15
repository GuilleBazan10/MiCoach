package com.micoach.workout.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MuscleJpaRepository extends JpaRepository<MuscleJpa, Long> {

    List<MuscleJpa> findAllByOrderByMuscleGroupAscNameAsc();
}
