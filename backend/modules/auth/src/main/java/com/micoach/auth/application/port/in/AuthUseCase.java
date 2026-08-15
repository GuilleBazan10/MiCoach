package com.micoach.auth.application.port.in;

import com.micoach.auth.presentation.dto.AuthResponse;
import com.micoach.auth.presentation.dto.LoginRequest;
import com.micoach.auth.presentation.dto.RefreshRequest;
import com.micoach.auth.presentation.dto.RegisterRequest;

/**
 * Puerto de entrada del módulo auth (casos de uso de autenticación).
 */
public interface AuthUseCase {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(RefreshRequest request);
}