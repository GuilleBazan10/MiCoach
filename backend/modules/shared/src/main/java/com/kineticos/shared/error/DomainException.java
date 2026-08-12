package com.kineticos.shared.error;

import lombok.Getter;

/**
 * Excepción base de dominio. Los módulos la extienden con su código y HTTP status.
 * El {@code GlobalExceptionHandler} de la app la traduce a {@link ApiError}.
 */
@Getter
public class DomainException extends RuntimeException {

    private final int status;
    private final ErrorCode code;

    public DomainException(int status, ErrorCode code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }
}
