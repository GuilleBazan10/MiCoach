package com.kineticos.user.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserInjuryJpaRepository extends JpaRepository<UserInjuryJpa, Long> {

    List<UserInjuryJpa> findByProfileId(Long profileId);
}