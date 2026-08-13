package com.kineticos.admin.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Entidad JPA de la tabla junction {@code admin_role_permissions}.
 */
@Entity
@Table(name = "admin_role_permissions")
@IdClass(RolePermissionId.class)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionJpa {

    @Id
    @Column(name = "role_id")
    private Long roleId;

    @Id
    @Column(name = "permission_id")
    private Long permissionId;
}
