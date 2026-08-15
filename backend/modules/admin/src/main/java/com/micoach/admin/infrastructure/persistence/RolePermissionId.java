package com.micoach.admin.infrastructure.persistence;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Clave compuesta de {@link RolePermissionJpa} (role_id, permission_id).
 */
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionId implements Serializable {

    private Long roleId;
    private Long permissionId;
}
