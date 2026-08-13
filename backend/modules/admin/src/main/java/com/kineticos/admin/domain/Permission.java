package com.kineticos.admin.domain;

import lombok.Getter;

import java.time.Instant;

/**
 * Permiso granular del sistema (tabla admin_permissions), ej: workout:read.
 */
@Getter
public class Permission {

    private final Long id;
    private final String code;
    private final String name;
    private final String description;
    private final Instant createdAt;

    private Permission(Long id, String code, String name, String description, Instant createdAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }

    public static Permission create(String code, String name, String description) {
        return new Permission(null, code, name, description, Instant.now());
    }

    public static Permission restore(Long id, String code, String name, String description,
                                     Instant createdAt) {
        return new Permission(id, code, name, description, createdAt);
    }
}
