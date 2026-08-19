package com.micoach.shared.event;

import java.time.Instant;
import java.util.Map;

/**
 * Evento publicado cuando se realiza una operación crítica o de funcionamiento relevante.
 * Es consumido por el módulo admin para guardarlo en la base de datos de auditoría.
 */
public record AuditLogEvent(
    Long userId,
    String action,
    String entityType,
    Long entityId,
    Map<String, Object> before,
    Map<String, Object> after,
    Instant occurredAt
) {
    public static AuditLogEvent of(Long userId, String action, String entityType, Long entityId,
                                   Map<String, Object> before, Map<String, Object> after) {
        return new AuditLogEvent(userId, action, entityType, entityId, before, after, Instant.now());
    }

    public static AuditLogEvent of(Long userId, String action, String entityType, Long entityId) {
        return new AuditLogEvent(userId, action, entityType, entityId, null, null, Instant.now());
    }
}
