package com.micoach.progress.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgressEntryJpaRepository extends JpaRepository<ProgressEntryJpa, Long> {

    List<ProgressEntryJpa> findByUserIdOrderByMeasuredAtDesc(Long userId);

    List<ProgressEntryJpa> findByUserIdAndMetricTypeOrderByMeasuredAtDesc(Long userId, String metricType);
}
