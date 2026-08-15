package com.micoach.user.domain;

import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Lesión con estado, para que la IA evite ejercicios de riesgo (tabla user_injuries).
 */
@Getter
public class UserInjury {

    private final Long id;
    private final Long profileId;
    private String bodyPart;
    private String injuryType;
    private String status;
    private String notes;
    private LocalDate occurredAt;
    private final Instant createdAt;

    private UserInjury(Long id, Long profileId, String bodyPart, String injuryType, String status,
                       String notes, LocalDate occurredAt, Instant createdAt) {
        this.id = id;
        this.profileId = profileId;
        this.bodyPart = bodyPart;
        this.injuryType = injuryType;
        this.status = status;
        this.notes = notes;
        this.occurredAt = occurredAt;
        this.createdAt = createdAt;
    }

    public static UserInjury create(Long profileId, String bodyPart, String injuryType,
                                    String status, String notes, LocalDate occurredAt) {
        return new UserInjury(null, profileId, bodyPart, injuryType, status, notes, occurredAt,
                Instant.now());
    }

    public static UserInjury restore(Long id, Long profileId, String bodyPart, String injuryType,
                                     String status, String notes, LocalDate occurredAt,
                                     Instant createdAt) {
        return new UserInjury(id, profileId, bodyPart, injuryType, status, notes, occurredAt,
                createdAt);
    }
}