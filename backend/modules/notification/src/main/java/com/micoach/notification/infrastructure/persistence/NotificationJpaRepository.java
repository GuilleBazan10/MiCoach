package com.micoach.notification.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationJpaRepository extends JpaRepository<NotificationJpa, Long> {

    List<NotificationJpa> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<NotificationJpa> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, String status);
}
