package com.kineticos.admin.domain;

import lombok.Getter;

import java.time.Instant;
import java.util.Map;

/**
 * Registro de auditoría de una operación crítica (tabla admin_audit_logs). Solo lectura
 * vía API por ahora: la escritura la usarán otros módulos cuando empiecen a auditar
 * (no hay callers todavía).
 */
@Getter
public class AuditLogEntry {

    private final Long id;
    private final Long userId;
    private final String action;
    private final String entityType;
    private final Long entityId;
    private final Map<String, Object> before;
    private final Map<String, Object> after;
    private final String ipAddress;
    private final String userAgent;
    private final String correlationId;
    private final Instant createdAt;

    private AuditLogEntry(Long id, Long userId, String action, String entityType, Long entityId,
                          Map<String, Object> before, Map<String, Object> after, String ipAddress,
                          String userAgent, String correlationId, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.before = before;
        this.after = after;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.correlationId = correlationId;
        this.createdAt = createdAt;
    }

    public static AuditLogEntry restore(Long id, Long userId, String action, String entityType, Long entityId,
                                        Map<String, Object> before, Map<String, Object> after, String ipAddress,
                                        String userAgent, String correlationId, Instant createdAt) {
        return new AuditLogEntry(id, userId, action, entityType, entityId, before, after, ipAddress, userAgent,
                correlationId, createdAt);
    }
}
