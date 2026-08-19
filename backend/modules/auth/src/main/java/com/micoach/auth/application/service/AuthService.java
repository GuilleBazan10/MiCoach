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
import com.micoach.shared.event.AuditLogEvent;
import com.micoach.shared.event.UserRegisteredEvent;
import com.micoach.shared.security.UserRoleProvider;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Implementación de los casos de uso de autenticación.
 * No conoce JPA ni HTTP: depende solo de puertos.
 */
@Slf4j
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
        log.info("Solicitud de registro recibida para el email: {}", email);
        userRepository.findByEmail(email).ifPresent(u -> {
            log.warn("Registro fallido: el email {} ya se encuentra registrado", email);
            throw new DomainException(409, ErrorCode.EMAIL_ALREADY_REGISTERED,
                    "El email ya está registrado");
        });

        AuthUser user = AuthUser.create(email, passwordEncoder.encode(request.password()));
        AuthUser saved = userRepository.save(user);

        log.info("Usuario registrado exitosamente con ID: {} y email: {}", saved.getId(), saved.getEmail());
        eventPublisher.publishEvent(UserRegisteredEvent.of(saved.getId(), saved.getEmail()));
        eventPublisher.publishEvent(AuditLogEvent.of(saved.getId(), "USER_REGISTER", "USER", saved.getId()));

        return buildAuthResponse(saved);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        log.info("Intento de inicio de sesión para el email: {}", email);
        
        AuthUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Credenciales inválidas: email {} no encontrado", email);
                    eventPublisher.publishEvent(AuditLogEvent.of(null, "USER_LOGIN_FAILED", "USER", null, Map.of("email", email), null));
                    return new DomainException(401, ErrorCode.INVALID_CREDENTIALS, "Credenciales inválidas");
                });

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.warn("Credenciales inválidas: contraseña incorrecta para el email {}", email);
            eventPublisher.publishEvent(AuditLogEvent.of(user.getId(), "USER_LOGIN_FAILED", "USER", user.getId(), Map.of("email", email), null));
            throw new DomainException(401, ErrorCode.INVALID_CREDENTIALS, "Credenciales inválidas");
        }

        user.ensureActive();
        user.recordLogin();
        AuthUser saved = userRepository.save(user);

        log.info("Inicio de sesión exitoso para el usuario ID: {} (email: {})", saved.getId(), saved.getEmail());
        eventPublisher.publishEvent(AuditLogEvent.of(saved.getId(), "USER_LOGIN", "USER", saved.getId()));

        return buildAuthResponse(saved);
    }

    @Override
    public AuthResponse refresh(RefreshRequest request) {
        Claims claims = jwtService.parse(request.refreshToken());
        if (!jwtService.isRefreshToken(claims)) {
            log.warn("Intento de refresco fallido: el token proporcionado no es un refresh token");
            throw new DomainException(401, ErrorCode.INVALID_TOKEN,
                    "Se requiere un refresh token");
        }

        Long userId = Long.valueOf(claims.getSubject());
        log.info("Solicitud de refresco de token para el usuario ID: {}", userId);
        AuthUser user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Intento de refresco fallido: usuario ID {} no encontrado", userId);
                    return new DomainException(401, ErrorCode.INVALID_TOKEN, "Usuario no encontrado");
                });
        user.ensureActive();

        log.info("Token refrescado exitosamente para el usuario ID: {}", userId);
        eventPublisher.publishEvent(AuditLogEvent.of(userId, "USER_TOKEN_REFRESH", "USER", userId));

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