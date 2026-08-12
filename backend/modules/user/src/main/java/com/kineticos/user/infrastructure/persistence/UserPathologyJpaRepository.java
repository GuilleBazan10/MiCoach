package com.kineticos.user.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPathologyJpaRepository extends JpaRepository<UserPathologyJpa, Long> {

    List<UserPathologyJpa> findByProfileId(Long profileId);
}