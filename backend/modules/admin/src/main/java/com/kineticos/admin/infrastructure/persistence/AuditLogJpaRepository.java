package com.kineticos.admin.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogJpaRepository extends JpaRepository<AuditLogJpa, Long> {

    List<AuditLogJpa> findAllByOrderByCreatedAtDesc();

    List<AuditLogJpa> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<AuditLogJpa> findByEntityTypeOrderByCreatedAtDesc(String entityType);

    List<AuditLogJpa> findByUserIdAndEntityTypeOrderByCreatedAtDesc(Long userId, String entityType);
}
