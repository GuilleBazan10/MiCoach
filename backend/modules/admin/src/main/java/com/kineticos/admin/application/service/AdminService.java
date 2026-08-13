package com.kineticos.admin.application.service;

import com.kineticos.shared.error.DomainException;
import com.kineticos.shared.error.ErrorCode;
import com.kineticos.admin.application.port.in.AdminUseCase;
import com.kineticos.admin.application.port.out.AdminRepository;
import com.kineticos.admin.domain.AuditLogEntry;
import com.kineticos.admin.domain.Permission;
import com.kineticos.admin.domain.Role;
import com.kineticos.admin.domain.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación de casos de uso del módulo admin. Depende solo del puerto de salida.
 */
@Service
public class AdminService implements AdminUseCase {

    private final AdminRepository repository;

    public AdminService(AdminRepository repository) {
        this.repository = repository;
    }

    // ------------------------- Roles y permisos -------------------------

    @Override
    @Transactional(readOnly = true)
    public List<Role> listRoles() {
        return repository.findRoles();
    }

    @Override
    @Transactional
    public Role createRole(RoleData data) {
        return repository.saveRole(Role.create(data.code(), data.name(), data.description()));
    }

    @Override
    @Transactional
    public void deleteRole(Long roleId) {
        Role role = repository.findRoleById(roleId)
                .orElseThrow(() -> new DomainException(404, ErrorCode.NOT_FOUND, "Rol no encontrado"));
        if (role.isSystem()) {
            throw new DomainException(409, ErrorCode.CONFLICT, "No se puede borrar un rol del sistema");
        }
        repository.deleteRole(roleId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> listPermissions() {
        return repository.findPermissions();
    }

    @Override
    @Transactional
    public Permission createPermission(PermissionData data) {
        return repository.savePermission(Permission.create(data.code(), data.name(), data.description()));
    }

    @Override
    @Transactional
    public void assignPermission(Long roleId, Long permissionId) {
        requireRole(roleId);
        requirePermission(permissionId);
        repository.assignPermissionToRole(roleId, permissionId);
    }

    @Override
    @Transactional
    public void unassignPermission(Long roleId, Long permissionId) {
        requireRole(roleId);
        requirePermission(permissionId);
        repository.unassignPermissionFromRole(roleId, permissionId);
    }

    private Role requireRole(Long roleId) {
        return repository.findRoleById(roleId)
                .orElseThrow(() -> new DomainException(404, ErrorCode.NOT_FOUND, "Rol no encontrado"));
    }

    private Permission requirePermission(Long permissionId) {
        return repository.findPermissionById(permissionId)
                .orElseThrow(() -> new DomainException(404, ErrorCode.NOT_FOUND, "Permiso no encontrado"));
    }

    // ------------------------- Roles de usuario -------------------------

    @Override
    @Transactional(readOnly = true)
    public List<UserRole> listUserRoles(Long userId) {
        return repository.findUserRoles(userId);
    }

    @Override
    @Transactional
    public void assignRole(Long userId, Long roleId) {
        requireRole(roleId);
        repository.assignRoleToUser(userId, roleId);
    }

    @Override
    @Transactional
    public void unassignRole(Long userId, Long roleId) {
        requireRole(roleId);
        repository.unassignRoleFromUser(userId, roleId);
    }

    // ------------------------- Auditoría -------------------------

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogEntry> listAuditLogs(AuditLogFilter filter) {
        return repository.findAuditLogs(filter);
    }
}
