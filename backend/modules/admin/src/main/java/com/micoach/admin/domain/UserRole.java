package com.micoach.admin.domain;

import lombok.Getter;

import java.time.Instant;

/**
 * Rol asignado a un usuario (tabla admin_user_roles), con datos del rol resueltos.
 */
@Getter
public class UserRole {

    private final Long userId;
    private final Long roleId;
    private final String roleCode;
    private final String roleName;
    private final Instant assignedAt;

    private UserRole(Long userId, Long roleId, String roleCode, String roleName, Instant assignedAt) {
        this.userId = userId;
        this.roleId = roleId;
        this.roleCode = roleCode;
        this.roleName = roleName;
        this.assignedAt = assignedAt;
    }

    public static UserRole restore(Long userId, Long roleId, String roleCode, String roleName,
                                   Instant assignedAt) {
        return new UserRole(userId, roleId, roleCode, roleName, assignedAt);
    }
}
