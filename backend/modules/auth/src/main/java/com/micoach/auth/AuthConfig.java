package com.micoach.auth;

import com.micoach.auth.infrastructure.security.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Wiring del módulo auth: habilita las propiedades JWT.
 * Los beans funcionales (JwtService, AuthService, repositorio) se descubren por
 * escaneo de componentes desde {@code com.micoach} (app).
 */
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class AuthConfig {
}
