package com.micoach.auth.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propiedades JWT desde {@code micoach.security.jwt.*} (application.yml / .env).
 */
@ConfigurationProperties(prefix = "micoach.security.jwt")
public record JwtProperties(String secret, long expirationMs, long refreshExpirationMs) {
}