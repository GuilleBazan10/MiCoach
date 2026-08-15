package com.micoach.user.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserGoalJpaRepository extends JpaRepository<UserGoalJpa, Long> {

    List<UserGoalJpa> findByProfileIdOrderByPriorityAsc(Long profileId);
}