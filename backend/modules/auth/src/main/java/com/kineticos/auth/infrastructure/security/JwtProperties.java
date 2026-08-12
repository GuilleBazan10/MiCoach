package com.kineticos.auth.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propiedades JWT desde {@code kineticos.security.jwt.*} (application.yml / .env).
 */
@ConfigurationProperties(prefix = "kineticos.security.jwt")
public record JwtProperties(String secret, long expirationMs, long refreshExpirationMs) {
}