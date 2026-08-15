package com.micoach.shared.security;

import java.util.List;

/**
 * Usuario autenticado expuesto en el SecurityContext (principal).
 * Los controllers lo inyectan con {@code @AuthenticationPrincipal}.
 */
public record AuthenticatedUser(Long id, String email, List<String> roles) {

    public boolean hasRole(String role) {
        return roles.contains(role);
    }
}
