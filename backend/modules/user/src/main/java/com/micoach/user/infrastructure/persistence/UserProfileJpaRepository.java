package com.micoach.user.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileJpaRepository extends JpaRepository<UserProfileJpa, Long> {

    Optional<UserProfileJpa> findByUserId(Long userId);
}