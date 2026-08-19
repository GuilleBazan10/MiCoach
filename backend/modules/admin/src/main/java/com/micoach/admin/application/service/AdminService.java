package com.micoach.admin.application.service;

import com.micoach.shared.error.DomainException;
import com.micoach.shared.error.ErrorCode;
import com.micoach.shared.event.AuditLogEvent;
import com.micoach.admin.application.port.in.AdminUseCase;
import com.micoach.admin.application.port.out.AdminRepository;
import com.micoach.admin.domain.AuditLogEntry;
import com.micoach.admin.domain.Permission;
import com.micoach.admin.domain.Role;
import com.micoach.admin.domain.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Implementación de casos de uso del módulo admin. Depende solo del puerto de salida.
 */
@Slf4j
@Service
public class AdminService implements AdminUseCase {

    private final AdminRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public AdminService(AdminRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
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
        log.info("Creando nuevo rol: {} (Nombre: {})", data.code(), data.name());
        Role role = Role.create(data.code(), data.name(), data.description());
        Role saved = repository.saveRole(role);
        
        log.info("Rol creado exitosamente con ID: {} (Código: {})", saved.getId(), saved.getCode());
        eventPublisher.publishEvent(AuditLogEvent.of(null, "ROLE_CREATE", "ROLE", saved.getId(), Map.of("code", data.code()), null));
        return saved;
    }

    @Override
    @Transactional
    public void deleteRole(Long roleId) {
        log.info("Eliminando rol ID: {}", roleId);
        Role role = repository.findRoleById(roleId)
                .orElseThrow(() -> {
                    log.warn("Eliminación de rol fallida: rol ID {} no encontrado", roleId);
                    return new DomainException(404, ErrorCode.NOT_FOUND, "Rol no encontrado");
                });
        if (role.isSystem()) {
            log.warn("Eliminación de rol fallida: no se puede borrar el rol del sistema ID {}", roleId);
            throw new DomainException(409, ErrorCode.CONFLICT, "No se puede borrar un rol del sistema");
        }
        repository.deleteRole(roleId);
        
        log.info("Rol ID: {} eliminado exitosamente", roleId);
        eventPublisher.publishEvent(AuditLogEvent.of(null, "ROLE_DELETE", "ROLE", roleId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> listPermissions() {
        return repository.findPermissions();
    }

    @Override
    @Transactional
    public Permission createPermission(PermissionData data) {
        log.info("Creando nuevo permiso: {} (Nombre: {})", data.code(), data.name());
        Permission permission = Permission.create(data.code(), data.name(), data.description());
        Permission saved = repository.savePermission(permission);
        
        log.info("Permiso creado exitosamente con ID: {} (Código: {})", saved.getId(), saved.getCode());
        eventPublisher.publishEvent(AuditLogEvent.of(null, "PERMISSION_CREATE", "PERMISSION", saved.getId(), Map.of("code", data.code()), null));
        return saved;
    }

    @Override
    @Transactional
    public void assignPermission(Long roleId, Long permissionId) {
        log.info("Asignando permiso ID: {} al rol ID: {}", permissionId, roleId);
        requireRole(roleId);
        requirePermission(permissionId);
        repository.assignPermissionToRole(roleId, permissionId);
        
        log.info("Permiso ID: {} asignado exitosamente al rol ID: {}", permissionId, roleId);
        eventPublisher.publishEvent(AuditLogEvent.of(null, "PERMISSION_ASSIGN", "ROLE", roleId, Map.of("permissionId", permissionId), null));
    }

    @Override
    @Transactional
    public void unassignPermission(Long roleId, Long permissionId) {
        log.info("Desasignando permiso ID: {} del rol ID: {}", permissionId, roleId);
        requireRole(roleId);
        requirePermission(permissionId);
        repository.unassignPermissionFromRole(roleId, permissionId);
        
        log.info("Permiso ID: {} desasignado exitosamente del rol ID: {}", permissionId, roleId);
        eventPublisher.publishEvent(AuditLogEvent.of(null, "PERMISSION_UNASSIGN", "ROLE", roleId, Map.of("permissionId", permissionId), null));
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
        log.info("Asignando rol ID: {} al usuario ID: {}", roleId, userId);
        requireRole(roleId);
        repository.assignRoleToUser(userId, roleId);
        
        log.info("Rol ID: {} asignado exitosamente al usuario ID: {}", roleId, userId);
        eventPublisher.publishEvent(AuditLogEvent.of(null, "ROLE_ASSIGN", "USER", userId, Map.of("roleId", roleId), null));
    }

    @Override
    @Transactional
    public void unassignRole(Long userId, Long roleId) {
        log.info("Desasignando rol ID: {} del usuario ID: {}", roleId, userId);
        requireRole(roleId);
        repository.unassignRoleFromUser(userId, roleId);
        
        log.info("Rol ID: {} desasignado exitosamente del usuario ID: {}", roleId, userId);
        eventPublisher.publishEvent(AuditLogEvent.of(null, "ROLE_UNASSIGN", "USER", userId, Map.of("roleId", roleId), null));
    }

    // ------------------------- Auditoría -------------------------

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogEntry> listAuditLogs(AuditLogFilter filter) {
        return repository.findAuditLogs(filter);
    }
}
