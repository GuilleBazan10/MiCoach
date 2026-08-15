package com.micoach.user.infrastructure.messaging;

import com.micoach.shared.event.UserRegisteredEvent;
import com.micoach.user.application.port.in.UserProfileUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Consume {@link UserRegisteredEvent} (publicado por auth) y crea el perfil de salud.
 * {@code AFTER_COMMIT}: si el registro se confirma, se crea el perfil en otra transacción.
 */
@Component
public class UserProfileOnRegisteredListener {

    private static final Logger log = LoggerFactory.getLogger(UserProfileOnRegisteredListener.class);

    private final UserProfileUseCase useCase;

    public UserProfileOnRegisteredListener(UserProfileUseCase useCase) {
        this.useCase = useCase;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserRegistered(UserRegisteredEvent event) {
        useCase.getOrCreateProfile(event.userId());
        log.info("Perfil vacío creado automáticamente para userId={}", event.userId());
    }
}