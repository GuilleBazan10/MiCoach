package com.kineticos.user.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserMedicationJpaRepository extends JpaRepository<UserMedicationJpa, Long> {

    List<UserMedicationJpa> findByProfileId(Long profileId);
}