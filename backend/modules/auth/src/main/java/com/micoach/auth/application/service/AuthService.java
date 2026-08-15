package com.micoach.auth.application.service;

import com.micoach.auth.application.port.in.AuthUseCase;
import com.micoach.auth.application.port.out.AuthUserRepository;
import com.micoach.auth.domain.AuthUser;
import com.micoach.auth.infrastructure.security.JwtService;
import com.micoach.auth.presentation.dto.AuthResponse;
import com.micoach.auth.presentation.dto.AuthUserResponse;
import com.micoach.auth.presentation.dto.LoginRequest;
import com.micoach.auth.presentation.dto.RefreshRequest;
import com.micoach.auth.presentation.dto.RegisterRequest;
import com.micoach.shared.error.DomainException;
import com.micoach.shared.error.ErrorCode;
import com.micoach.shared.event.UserRegisteredEvent;
import com.micoach.shared.security.UserRoleProvider;
import io.jsonwebtoken.Claims;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación de los casos de uso de autenticación.
 * No conoce JPA ni HTTP: depende solo de puertos.
 */
@Service
public class AuthService implements AuthUseCase {

    private final AuthUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ApplicationEventPublisher eventPublisher;
    private final UserRoleProvider roleProvider;

    public AuthService(AuthUserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, ApplicationEventPublisher eventPublisher,
                       UserRoleProvider roleProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.eventPublisher = eventPublisher;
        this.roleProvider = roleProvider;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        userRepository.findByEmail(email).ifPresent(u -> {
            throw new DomainException(409, ErrorCode.EMAIL_ALREADY_REGISTERED,
                    "El email ya está registrado");
        });

        AuthUser user = AuthUser.create(email, passwordEncoder.encode(request.password()));
        AuthUser saved = userRepository.save(user);

        eventPublisher.publishEvent(UserRegisteredEvent.of(saved.getId(), saved.getEmail()));

        return buildAuthResponse(saved);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        AuthUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DomainException(401, ErrorCode.INVALID_CREDENTIALS,
                        "Credenciales inválidas"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new DomainException(401, ErrorCode.INVALID_CREDENTIALS,
                    "Credenciales inválidas");
        }

        user.ensureActive();
        user.recordLogin();
        AuthUser saved = userRepository.save(user);

        return buildAuthResponse(saved);
    }

    @Override
    public AuthResponse refresh(RefreshRequest request) {
        Claims claims = jwtService.parse(request.refreshToken());
        if (!jwtService.isRefreshToken(claims)) {
            throw new DomainException(401, ErrorCode.INVALID_TOKEN,
                    "Se requiere un refresh token");
        }

        Long userId = Long.valueOf(claims.getSubject());
        AuthUser user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException(401, ErrorCode.INVALID_TOKEN,
                        "Usuario no encontrado"));
        user.ensureActive();

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(AuthUser user) {
        List<String> roles = roleProvider.rolesFor(user.getId());
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(), roles);
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());
        return new AuthResponse(accessToken, refreshToken, "Bearer", 15 * 60,
                new AuthUserResponse(user.getId(), user.getEmail(), roles));
    }
}