package com.kineticos.auth.presentation.dto;

import java.util.List;

public record AuthUserResponse(Long id, String email, List<String> roles) {
}