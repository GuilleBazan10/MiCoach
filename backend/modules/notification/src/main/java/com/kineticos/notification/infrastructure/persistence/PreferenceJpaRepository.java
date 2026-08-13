package com.kineticos.notification.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PreferenceJpaRepository extends JpaRepository<PreferenceJpa, Long> {

    List<PreferenceJpa> findByUserId(Long userId);

    Optional<PreferenceJpa> findByUserIdAndEventTypeAndChannel(Long userId, String eventType, String channel);
}
