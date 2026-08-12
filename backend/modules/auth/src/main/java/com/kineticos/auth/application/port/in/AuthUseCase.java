package com.kineticos.auth.application.port.in;

import com.kineticos.auth.presentation.dto.AuthResponse;
import com.kineticos.auth.presentation.dto.LoginRequest;
import com.kineticos.auth.presentation.dto.RefreshRequest;
import com.kineticos.auth.presentation.dto.RegisterRequest;

/**
 * Puerto de entrada del módulo auth (casos de uso de autenticación).
 */
public interface AuthUseCase {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(RefreshRequest request);
}