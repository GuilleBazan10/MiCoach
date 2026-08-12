package com.kineticos.shared.error;

import java.time.Instant;

/**
 * Formato unificado de respuesta de error (docs/03-api-contracts.md).
 */
public record ApiError(Instant timestamp, int status, ErrorCode code, String message, String path) {

    public static ApiError of(int status, ErrorCode code, String message, String path) {
        return new ApiError(Instant.now(), status, code, message, path);
    }
}
