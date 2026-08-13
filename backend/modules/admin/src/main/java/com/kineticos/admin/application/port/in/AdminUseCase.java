package com.kineticos.admin.application.port.in;

import com.kineticos.admin.domain.AuditLogEntry;
import com.kineticos.admin.domain.Permission;
import com.kineticos.admin.domain.Role;
import com.kineticos.admin.domain.UserRole;

import java.util.List;

/**
 * Puerto de entrada del módulo admin (roles, permisos, asignaciones y auditoría).
 */
public interface AdminUseCase {

    // ------------------------- Roles y permisos -------------------------

    List<Role> listRoles();

    Role createRole(RoleData data);

    void deleteRole(Long roleId);

    List<Permission> listPermissions();

    Permission createPermission(PermissionData data);

    void assignPermission(Long roleId, Long permissionId);

    void unassignPermission(Long roleId, Long permissionId);

    // ------------------------- Roles de usuario -------------------------

    List<UserRole> listUserRoles(Long userId);

    void assignRole(Long userId, Long roleId);

    void unassignRole(Long userId, Long roleId);

    // ------------------------- Auditoría -------------------------

    List<AuditLogEntry> listAuditLogs(AuditLogFilter filter);

    record RoleData(String code, String name, String description) {
    }

    record PermissionData(String code, String name, String description) {
    }

    record AuditLogFilter(Long userId, String entityType) {
    }
}
