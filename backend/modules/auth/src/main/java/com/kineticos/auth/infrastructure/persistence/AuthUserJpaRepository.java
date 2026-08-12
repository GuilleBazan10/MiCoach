package com.kineticos.auth.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthUserJpaRepository extends JpaRepository<AuthUserJpa, Long> {

    Optional<AuthUserJpa> findByEmail(String email);
}