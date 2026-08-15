package com.micoach.auth.presentation;

import com.micoach.auth.application.port.in.AuthUseCase;
import com.micoach.auth.presentation.dto.AuthResponse;
import com.micoach.auth.presentation.dto.AuthUserResponse;
import com.micoach.auth.presentation.dto.LoginRequest;
import com.micoach.auth.presentation.dto.RefreshRequest;
import com.micoach.auth.presentation.dto.RegisterRequest;
import com.micoach.shared.security.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Contratos REST del módulo auth (base path /api/v1/auth).
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authUseCase.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authUseCase.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authUseCase.refresh(request);
    }

    @GetMapping("/me")
    public AuthUserResponse me(@AuthenticationPrincipal AuthenticatedUser principal) {
        return new AuthUserResponse(principal.id(), principal.email(), principal.roles());
    }
}