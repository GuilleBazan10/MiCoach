package com.micoach.auth.domain;

import com.micoach.shared.error.DomainException;
import com.micoach.shared.error.ErrorCode;
import lombok.Getter;

import java.time.Instant;

/**
 * Entidad de dominio AuthUser: identidad y credenciales.
 * Almacena solo datos de autenticación; el perfil de salud vive en el módulo user.
 */
@Getter
public class AuthUser {

    private Long id;
    private String email;
    private String passwordHash;
    private boolean emailVerified;
    private String status;
    private Instant lastLoginAt;
    private final Instant createdAt;
    private Instant updatedAt;

    AuthUser(Long id, String email, String passwordHash, boolean emailVerified,
             String status, Instant lastLoginAt, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.emailVerified = emailVerified;
        this.status = status;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Reconstrucción desde persistencia (usado por el adaptador JPA).
     */
    public static AuthUser restore(Long id, String email, String passwordHash, boolean emailVerified,
                                   String status, Instant lastLoginAt, Instant createdAt, Instant updatedAt) {
        return new AuthUser(id, email, passwordHash, emailVerified, status, lastLoginAt, createdAt, updatedAt);
    }

    public static AuthUser create(String email, String encodedPassword) {
        AuthUser user = new AuthUser(null, email, encodedPassword, false, "active",
                null, Instant.now(), Instant.now());
        user.updatedAt = user.createdAt;
        return user;
    }

    public void markEmailVerified() {
        this.emailVerified = true;
    }

    public void recordLogin() {
        this.lastLoginAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void ensureActive() {
        if (!"active".equals(this.status)) {
            throw new DomainException(403, ErrorCode.UNAUTHORIZED, "Cuenta " + this.status);
        }
    }
}