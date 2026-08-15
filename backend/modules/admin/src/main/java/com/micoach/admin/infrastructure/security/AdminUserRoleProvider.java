package com.micoach.admin.infrastructure.security;

import com.micoach.admin.application.port.out.AdminRepository;
import com.micoach.admin.domain.UserRole;
import com.micoach.shared.security.UserRoleProvider;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Implementación real de {@link UserRoleProvider}: resuelve los roles asignados en
 * {@code admin_user_roles}. Un usuario sin roles asignados (caso normal de un
 * registro recién creado) cae en ROLE_USER por defecto.
 */
@Component
public class AdminUserRoleProvider implements UserRoleProvider {

    private final AdminRepository repository;

    public AdminUserRoleProvider(AdminRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<String> rolesFor(Long userId) {
        List<String> roles = repository.findUserRoles(userId).stream().map(UserRole::getRoleCode).toList();
        return roles.isEmpty() ? List.of("ROLE_USER") : roles;
    }
}
