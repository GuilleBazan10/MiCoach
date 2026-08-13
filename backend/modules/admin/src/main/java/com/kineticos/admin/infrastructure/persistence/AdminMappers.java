package com.kineticos.admin.infrastructure.persistence;

import com.kineticos.admin.domain.AuditLogEntry;
import com.kineticos.admin.domain.Permission;
import com.kineticos.admin.domain.Role;
import com.kineticos.admin.domain.UserRole;

import java.util.List;

final class RoleMapper {

    private RoleMapper() {
    }

    static Role toDomain(RoleJpa jpa, List<String> permissionCodes) {
        return Role.restore(jpa.getId(), jpa.getCode(), jpa.getName(), jpa.getDescription(), jpa.isSystem(),
                permissionCodes, jpa.getCreatedAt());
    }

    static RoleJpa toJpa(Role domain) {
        return RoleJpa.builder()
                .id(domain.getId())
                .code(domain.getCode())
                .name(domain.getName())
                .description(domain.getDescription())
                .system(domain.isSystem())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}

final class PermissionMapper {

    private PermissionMapper() {
    }

    static Permission toDomain(PermissionJpa jpa) {
        return Permission.restore(jpa.getId(), jpa.getCode(), jpa.getName(), jpa.getDescription(),
                jpa.getCreatedAt());
    }

    static PermissionJpa toJpa(Permission domain) {
        return PermissionJpa.builder()
                .id(domain.getId())
                .code(domain.getCode())
                .name(domain.getName())
                .description(domain.getDescription())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}

final class UserRoleMapper {

    private UserRoleMapper() {
    }

    static UserRole toDomain(UserRoleJpa jpa, RoleJpa role) {
        return UserRole.restore(jpa.getUserId(), jpa.getRoleId(), role.getCode(), role.getName(),
                jpa.getAssignedAt());
    }
}

final class AuditLogMapper {

    private AuditLogMapper() {
    }

    static AuditLogEntry toDomain(AuditLogJpa jpa) {
        return AuditLogEntry.restore(jpa.getId(), jpa.getUserId(), jpa.getAction(), jpa.getEntityType(),
                jpa.getEntityId(), jpa.getBefore(), jpa.getAfter(), jpa.getIpAddress(), jpa.getUserAgent(),
                jpa.getCorrelationId(), jpa.getCreatedAt());
    }
}
