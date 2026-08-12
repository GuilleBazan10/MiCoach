package com.kineticos.user.domain;

import lombok.Getter;

import java.time.Instant;

/**
 * Medicación actual del usuario (tabla user_medications).
 */
@Getter
public class UserMedication {

    private final Long id;
    private final Long profileId;
    private String medicationName;
    private String dosage;
    private String schedule;
    private String notes;
    private boolean active;
    private final Instant createdAt;
    private Instant updatedAt;

    private UserMedication(Long id, Long profileId, String medicationName, String dosage,
                           String schedule, String notes, boolean active,
                           Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.profileId = profileId;
        this.medicationName = medicationName;
        this.dosage = dosage;
        this.schedule = schedule;
        this.notes = notes;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UserMedication create(Long profileId, String medicationName, String dosage,
                                        String schedule, String notes) {
        Instant now = Instant.now();
        return new UserMedication(null, profileId, medicationName, dosage, schedule, notes,
                true, now, now);
    }

    public static UserMedication restore(Long id, Long profileId, String medicationName,
                                         String dosage, String schedule, String notes, boolean active,
                                         Instant createdAt, Instant updatedAt) {
        return new UserMedication(id, profileId, medicationName, dosage, schedule, notes, active,
                createdAt, updatedAt);
    }
}