package com.kineticos.auth.infrastructure.persistence;

import com.kineticos.auth.application.port.out.AuthUserRepository;
import com.kineticos.auth.domain.AuthUser;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adaptador JPA del puerto {@link AuthUserRepository}.
 */
@Component
public class AuthUserRepositoryAdapter implements AuthUserRepository {

    private final AuthUserJpaRepository jpaRepository;

    public AuthUserRepositoryAdapter(AuthUserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<AuthUser> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(AuthUserRepositoryAdapter::toDomain);
    }

    @Override
    public Optional<AuthUser> findById(Long id) {
        return jpaRepository.findById(id).map(AuthUserRepositoryAdapter::toDomain);
    }

    @Override
    public AuthUser save(AuthUser user) {
        AuthUserJpa saved = jpaRepository.save(toJpa(user));
        return toDomain(saved);
    }

    private static AuthUser toDomain(AuthUserJpa jpa) {
        return AuthUser.restore(jpa.getId(), jpa.getEmail(), jpa.getPasswordHash(),
                jpa.getEmailVerifiedAt() != null, jpa.getStatus(), jpa.getLastLoginAt(),
                jpa.getCreatedAt(), jpa.getUpdatedAt());
    }

    private static AuthUserJpa toJpa(AuthUser domain) {
        return AuthUserJpa.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .passwordHash(domain.getPasswordHash())
                .emailVerifiedAt(domain.isEmailVerified() ? domain.getCreatedAt() : null)
                .status(domain.getStatus())
                .lastLoginAt(domain.getLastLoginAt())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}