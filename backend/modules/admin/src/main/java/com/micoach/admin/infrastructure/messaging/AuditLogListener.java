package com.micoach.admin.infrastructure.messaging;

import com.micoach.admin.infrastructure.persistence.AuditLogJpa;
import com.micoach.admin.infrastructure.persistence.AuditLogJpaRepository;
import com.micoach.shared.event.AuditLogEvent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Escucha eventos de auditoría y los persiste en la base de datos de administración,
 * además de imprimirlos en la consola del backend.
 */
@Slf4j
@Component
public class AuditLogListener {

    private final AuditLogJpaRepository auditLogRepository;

    public AuditLogListener(AuditLogJpaRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @EventListener
    public void onAuditLog(AuditLogEvent event) {
        String ipAddress = null;
        String userAgent = null;
        String correlationId = null;

        // Extraer metadatos web de forma automática si la llamada ocurre dentro del hilo de un request HTTP
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String xff = request.getHeader("X-Forwarded-For");
                if (xff != null && !xff.isBlank()) {
                    ipAddress = xff.split(",")[0].trim();
                } else {
                    ipAddress = request.getRemoteAddr();
                }
                userAgent = request.getHeader("User-Agent");
                correlationId = request.getHeader("X-Correlation-Id");
            }
        } catch (Exception e) {
            log.warn("No se pudo obtener el contexto de la solicitud HTTP para la auditoría: {}", e.getMessage());
        }

        // Mostrar el log de funcionamiento en el backend
        log.info("[AUDIT LOG] Action: {} | User: {} | Entity: {}({}) | IP: {} | User-Agent: {} | Correlation: {}",
                event.action(),
                event.userId() != null ? event.userId() : "ANONYMOUS",
                event.entityType(),
                event.entityId() != null ? event.entityId() : "N/A",
                ipAddress != null ? ipAddress : "N/A",
                userAgent != null ? userAgent : "N/A",
                correlationId != null ? correlationId : "N/A");

        // Construir la entidad JPA y guardarla
        AuditLogJpa entity = AuditLogJpa.builder()
                .userId(event.userId())
                .action(event.action())
                .entityType(event.entityType())
                .entityId(event.entityId())
                .before(event.before())
                .after(event.after())
                .ipAddress(ipAddress != null && ipAddress.length() > 45 ? ipAddress.substring(0, 45) : ipAddress)
                .userAgent(userAgent != null && userAgent.length() > 255 ? userAgent.substring(0, 255) : userAgent)
                .correlationId(correlationId != null && correlationId.length() > 36 ? correlationId.substring(0, 36) : correlationId)
                .createdAt(event.occurredAt())
                .build();

        try {
            auditLogRepository.save(entity);
        } catch (Exception e) {
            log.error("Error al persistir el registro de auditoría en base de datos: {}", e.getMessage(), e);
        }
    }
}
