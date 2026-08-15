package com.micoach.admin.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RolePermissionJpaRepository extends JpaRepository<RolePermissionJpa, RolePermissionId> {

    List<RolePermissionJpa> findByRoleId(Long roleId);

    List<RolePermissionJpa> findByRoleIdIn(List<Long> roleIds);

    Optional<RolePermissionJpa> findByRoleIdAndPermissionId(Long roleId, Long permissionId);
}
