package com.micoach.auth.application.port.out;

import com.micoach.auth.domain.AuthUser;
import java.util.Optional;

public interface AuthUserRepository {
    Optional<AuthUser> findByEmail(String email);
    Optional<AuthUser> findById(Long id);
    AuthUser save(AuthUser user);
}
