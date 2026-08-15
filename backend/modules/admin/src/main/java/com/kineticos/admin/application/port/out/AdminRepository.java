package com.kineticos.admin.application.port.out;

import com.kineticos.admin.application.port.in.AdminUseCase.AuditLogFilter;
import com.kineticos.admin.domain.*;
import java.util.List;
import java.util.Optional;

public interface AdminRepository {
    List<Role> findRoles();
    Optional<Role> findRoleById(Long roleId);
    Role saveRole(Role role);
    void deleteRole(Long roleId);
    List<Permission> findPermissions();
    Optional<Permission> findPermissionById(Long permissionId);
    Permission savePermission(Permission permission);
    void assignPermissionToRole(Long roleId, Long permissionId);
    void unassignPermissionFromRole(Long roleId, Long permissionId);
    List<UserRole> findUserRoles(Long userId);
    void assignRoleToUser(Long userId, Long roleId);
    void unassignRoleFromUser(Long userId, Long roleId);
    List<AuditLogEntry> findAuditLogs(AuditLogFilter filter);
}
