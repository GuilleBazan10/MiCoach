package com.kineticos.shared.security;

import java.util.List;

/**
 * Resuelve los roles reales de un usuario (tabla {@code admin_user_roles} del
 * módulo admin) para que el módulo auth pueda emitirlos en el JWT sin depender
 * directamente de admin — la implementación vive en admin y Spring la inyecta
 * acá por tipo.
 */
public interface UserRoleProvider {

    List<String> rolesFor(Long userId);
}
