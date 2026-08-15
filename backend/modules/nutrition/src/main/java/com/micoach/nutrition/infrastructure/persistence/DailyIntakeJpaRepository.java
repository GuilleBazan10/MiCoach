package com.micoach.nutrition.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailyIntakeJpaRepository extends JpaRepository<DailyIntakeJpa, Long> {

    List<DailyIntakeJpa> findByUserIdAndFoodDateOrderByConsumedAtDesc(Long userId, LocalDate foodDate);

    List<DailyIntakeJpa> findByUserIdOrderByFoodDateDescConsumedAtDesc(Long userId);
}
