package com.kineticos.admin.infrastructure.persistence;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Clave compuesta de {@link UserRoleJpa} (user_id, role_id).
 */
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleId implements Serializable {

    private Long userId;
    private Long roleId;
}
