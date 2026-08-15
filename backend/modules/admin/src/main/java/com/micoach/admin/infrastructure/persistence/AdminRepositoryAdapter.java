package com.micoach.admin.infrastructure.persistence;

import com.micoach.admin.application.port.in.AdminUseCase.AuditLogFilter;
import com.micoach.admin.application.port.out.AdminRepository;
import com.micoach.admin.domain.AuditLogEntry;
import com.micoach.admin.domain.Permission;
import com.micoach.admin.domain.Role;
import com.micoach.admin.domain.UserRole;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Adaptador JPA del puerto {@link AdminRepository}.
 */
@Component
public class AdminRepositoryAdapter implements AdminRepository {

    private final RoleJpaRepository roleRepository;
    private final PermissionJpaRepository permissionRepository;
    private final RolePermissionJpaRepository rolePermissionRepository;
    private final UserRoleJpaRepository userRoleRepository;
    private final AuditLogJpaRepository auditLogRepository;

    public AdminRepositoryAdapter(RoleJpaRepository roleRepository,
                                  PermissionJpaRepository permissionRepository,
                                  RolePermissionJpaRepository rolePermissionRepository,
                                  UserRoleJpaRepository userRoleRepository,
                                  AuditLogJpaRepository auditLogRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.userRoleRepository = userRoleRepository;
        this.auditLogRepository = auditLogRepository;
    }

    // ------------------------- Roles y permisos -------------------------

    @Override
    public List<Role> findRoles() {
        List<RoleJpa> roles = roleRepository.findAll();
        Map<Long, List<String>> codesByRole = loadPermissionCodes(roles.stream().map(RoleJpa::getId).toList());
        return roles.stream().map(r -> RoleMapper.toDomain(r, codesByRole.getOrDefault(r.getId(), List.of())))
                .toList();
    }

    @Override
    public Optional<Role> findRoleById(Long roleId) {
        return roleRepository.findById(roleId)
                .map(r -> RoleMapper.toDomain(r, loadPermissionCodes(List.of(roleId)).getOrDefault(roleId, List.of())));
    }

    @Override
    public Role saveRole(Role role) {
        RoleJpa saved = roleRepository.save(RoleMapper.toJpa(role));
        return RoleMapper.toDomain(saved, role.getPermissionCodes());
    }

    @Override
    public void deleteRole(Long roleId) {
        roleRepository.deleteById(roleId);
    }

    private Map<Long, List<String>> loadPermissionCodes(List<Long> roleIds) {
        if (roleIds.isEmpty()) {
            return Map.of();
        }
        List<RolePermissionJpa> links = rolePermissionRepository.findByRoleIdIn(roleIds);
        if (links.isEmpty()) {
            return Map.of();
        }
        Set<Long> permissionIds = links.stream().map(RolePermissionJpa::getPermissionId).collect(Collectors.toSet());
        Map<Long, PermissionJpa> permissionsById = permissionRepository.findAllById(permissionIds).stream()
                .collect(Collectors.toMap(PermissionJpa::getId, p -> p));

        return links.stream().collect(Collectors.groupingBy(RolePermissionJpa::getRoleId, LinkedHashMap::new,
                Collectors.mapping(link -> permissionsById.get(link.getPermissionId()).getCode(),
                        Collectors.toList())));
    }

    @Override
    public List<Permission> findPermissions() {
        return permissionRepository.findAll().stream().map(PermissionMapper::toDomain).toList();
    }

    @Override
    public Optional<Permission> findPermissionById(Long permissionId) {
        return permissionRepository.findById(permissionId).map(PermissionMapper::toDomain);
    }

    @Override
    public Permission savePermission(Permission permission) {
        return PermissionMapper.toDomain(permissionRepository.save(PermissionMapper.toJpa(permission)));
    }

    @Override
    public void assignPermissionToRole(Long roleId, Long permissionId) {
        rolePermissionRepository.save(RolePermissionJpa.builder().roleId(roleId).permissionId(permissionId).build());
    }

    @Override
    public void unassignPermissionFromRole(Long roleId, Long permissionId) {
        rolePermissionRepository.findByRoleIdAndPermissionId(roleId, permissionId)
                .ifPresent(rolePermissionRepository::delete);
    }

    // ------------------------- Roles de usuario -------------------------

    @Override
    public List<UserRole> findUserRoles(Long userId) {
        List<UserRoleJpa> assignments = userRoleRepository.findByUserId(userId);
        if (assignments.isEmpty()) {
            return List.of();
        }
        Map<Long, RoleJpa> rolesById = roleRepository.findAllById(
                assignments.stream().map(UserRoleJpa::getRoleId).toList()).stream()
                .collect(Collectors.toMap(RoleJpa::getId, r -> r));
        return assignments.stream().map(a -> UserRoleMapper.toDomain(a, rolesById.get(a.getRoleId()))).toList();
    }

    @Override
    public void assignRoleToUser(Long userId, Long roleId) {
        userRoleRepository.save(UserRoleJpa.builder().userId(userId).roleId(roleId).assignedAt(Instant.now()).build());
    }

    @Override
    public void unassignRoleFromUser(Long userId, Long roleId) {
        userRoleRepository.findByUserIdAndRoleId(userId, roleId).ifPresent(userRoleRepository::delete);
    }

    // ------------------------- Auditoría -------------------------

    @Override
    public List<AuditLogEntry> findAuditLogs(AuditLogFilter filter) {
        List<AuditLogJpa> logs;
        if (filter.userId() != null && filter.entityType() != null) {
            logs = auditLogRepository.findByUserIdAndEntityTypeOrderByCreatedAtDesc(filter.userId(), filter.entityType());
        } else if (filter.userId() != null) {
            logs = auditLogRepository.findByUserIdOrderByCreatedAtDesc(filter.userId());
        } else if (filter.entityType() != null) {
            logs = auditLogRepository.findByEntityTypeOrderByCreatedAtDesc(filter.entityType());
        } else {
            logs = auditLogRepository.findAllByOrderByCreatedAtDesc();
        }
        return logs.stream().map(AuditLogMapper::toDomain).toList();
    }
}
