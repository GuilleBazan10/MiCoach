package com.micoach.admin.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRoleJpaRepository extends JpaRepository<UserRoleJpa, UserRoleId> {

    List<UserRoleJpa> findByUserId(Long userId);

    Optional<UserRoleJpa> findByUserIdAndRoleId(Long userId, Long roleId);
}
