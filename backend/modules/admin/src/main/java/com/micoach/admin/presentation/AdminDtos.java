package com.micoach.admin.presentation;

import com.micoach.admin.domain.AuditLogEntry;
import com.micoach.admin.domain.Permission;
import com.micoach.admin.domain.Role;
import com.micoach.admin.domain.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * DTOs del módulo admin. Cada verbose class es un contrato de entrada/salida.
 */
public final class AdminDtos {

    private AdminDtos() {
    }

    public record RoleResponse(Long id, String code, String name, String description, boolean system,
                               List<String> permissionCodes) {

        static RoleResponse from(Role r) {
            return new RoleResponse(r.getId(), r.getCode(), r.getName(), r.getDescription(), r.isSystem(),
                    r.getPermissionCodes());
        }
    }

    public record RoleRequest(@NotBlank @Size(max = 50) String code, @NotBlank @Size(max = 100) String name,
                              @Size(max = 300) String description) {
    }

    public record PermissionResponse(Long id, String code, String name, String description) {

        static PermissionResponse from(Permission p) {
            return new PermissionResponse(p.getId(), p.getCode(), p.getName(), p.getDescription());
        }
    }

    public record PermissionRequest(@NotBlank @Size(max = 80) String code, @NotBlank @Size(max = 150) String name,
                                    @Size(max = 300) String description) {
    }

    public record UserRoleResponse(Long roleId, String roleCode, String roleName, Instant assignedAt) {

        static UserRoleResponse from(UserRole ur) {
            return new UserRoleResponse(ur.getRoleId(), ur.getRoleCode(), ur.getRoleName(), ur.getAssignedAt());
        }
    }

    public record AuditLogResponse(Long id, Long userId, String action, String entityType, Long entityId,
                                   Map<String, Object> before, Map<String, Object> after, String correlationId,
                                   Instant createdAt) {

        static AuditLogResponse from(AuditLogEntry e) {
            return new AuditLogResponse(e.getId(), e.getUserId(), e.getAction(), e.getEntityType(),
                    e.getEntityId(), e.getBefore(), e.getAfter(), e.getCorrelationId(), e.getCreatedAt());
        }
    }
}
