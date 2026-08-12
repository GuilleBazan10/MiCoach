package com.kineticos.user.domain;

import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Patología diagnosticada del usuario (tabla user_pathologies).
 */
@Getter
public class UserPathology {

    private final Long id;
    private final Long profileId;
    private String pathology;
    private String notes;
    private LocalDate diagnosedAt;
    private final Instant createdAt;

    private UserPathology(Long id, Long profileId, String pathology, String notes,
                          LocalDate diagnosedAt, Instant createdAt) {
        this.id = id;
        this.profileId = profileId;
        this.pathology = pathology;
        this.notes = notes;
        this.diagnosedAt = diagnosedAt;
        this.createdAt = createdAt;
    }

    public static UserPathology create(Long profileId, String pathology, String notes,
                                       LocalDate diagnosedAt) {
        return new UserPathology(null, profileId, pathology, notes, diagnosedAt, Instant.now());
    }

    public static UserPathology restore(Long id, Long profileId, String pathology, String notes,
                                        LocalDate diagnosedAt, Instant createdAt) {
        return new UserPathology(id, profileId, pathology, notes, diagnosedAt, createdAt);
    }
}