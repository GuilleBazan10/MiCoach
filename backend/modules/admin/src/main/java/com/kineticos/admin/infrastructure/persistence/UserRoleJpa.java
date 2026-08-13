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

import java.time.Instant;

/**
 * Entidad JPA de la tabla junction {@code admin_user_roles}.
 */
@Entity
@Table(name = "admin_user_roles")
@IdClass(UserRoleId.class)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleJpa {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "assigned_at", nullable = false)
    private Instant assignedAt;
}
