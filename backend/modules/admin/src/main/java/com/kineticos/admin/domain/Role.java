package com.kineticos.admin.domain;

import lombok.Getter;

import java.time.Instant;
import java.util.List;

/**
 * Rol del sistema (tabla admin_roles), con los códigos de permiso asignados.
 */
@Getter
public class Role {

    private final Long id;
    private final String code;
    private final String name;
    private final String description;
    private final boolean system;
    private final List<String> permissionCodes;
    private final Instant createdAt;

    private Role(Long id, String code, String name, String description, boolean system,
                List<String> permissionCodes, Instant createdAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.system = system;
        this.permissionCodes = permissionCodes == null ? List.of() : permissionCodes;
        this.createdAt = createdAt;
    }

    public static Role create(String code, String name, String description) {
        return new Role(null, code, name, description, false, List.of(), Instant.now());
    }

    public static Role restore(Long id, String code, String name, String description, boolean system,
                               List<String> permissionCodes, Instant createdAt) {
        return new Role(id, code, name, description, system, permissionCodes, createdAt);
    }
}
