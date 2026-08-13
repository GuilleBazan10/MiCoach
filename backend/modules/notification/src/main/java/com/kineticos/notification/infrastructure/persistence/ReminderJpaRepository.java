package com.kineticos.notification.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReminderJpaRepository extends JpaRepository<ReminderJpa, Long> {

    List<ReminderJpa> findByUserId(Long userId);
}
