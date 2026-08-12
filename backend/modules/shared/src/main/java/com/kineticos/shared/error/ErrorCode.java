package com.kineticos.shared.error;

/**
 * Códigos de error normalizados de la API.
 * Se exponen en el campo {@code code} de {@link ApiError} (ver docs/03-api-contracts.md).
 */
public enum ErrorCode {

    VALIDATION_ERROR,
    INVALID_CREDENTIALS,
    EMAIL_ALREADY_REGISTERED,
    USER_NOT_FOUND,
    INVALID_TOKEN,
    TOKEN_EXPIRED,
    UNAUTHORIZED,
    NOT_FOUND,
    CONFLICT,
    INTERNAL_ERROR
}
