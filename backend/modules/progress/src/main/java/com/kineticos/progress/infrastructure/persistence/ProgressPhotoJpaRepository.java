package com.kineticos.progress.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgressPhotoJpaRepository extends JpaRepository<ProgressPhotoJpa, Long> {

    List<ProgressPhotoJpa> findByUserIdOrderByTakenAtDesc(Long userId);
}
