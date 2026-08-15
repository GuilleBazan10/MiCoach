package com.kineticos.auth.application.port.out;

import com.kineticos.auth.domain.AuthUser;
import java.util.Optional;

public interface AuthUserRepository {
    Optional<AuthUser> findByEmail(String email);
    Optional<AuthUser> findById(Long id);
    AuthUser save(AuthUser user);
}
