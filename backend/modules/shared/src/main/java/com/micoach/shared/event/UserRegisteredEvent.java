package com.micoach.shared.event;

import java.time.Instant;

/**
 * Evento publicado cuando un usuario se registra (fuente: módulo auth).
 * El módulo user lo consume para crear el perfil de salud por defecto.
 * En el futuro viaja por RabbitMQ (outbox pattern); por ahora es un evento en proceso.
 */
public record UserRegisteredEvent(Long userId, String email, Instant occurredAt) {

    public static UserRegisteredEvent of(Long userId, String email) {
        return new UserRegisteredEvent(userId, email, Instant.now());
    }
}