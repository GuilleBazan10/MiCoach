package com.micoach.progress.domain;

import lombok.Getter;

import java.time.Instant;

/**
 * Foto de progreso físico (tabla progress_photos), almacenada en MinIO/CDN.
 */
@Getter
public class ProgressPhoto {

    private final Long id;
    private final Long userId;
    private final String photoUrl;
    private final String angle;
    private final Instant takenAt;
    private final String notes;
    private final Instant createdAt;

    private ProgressPhoto(Long id, Long userId, String photoUrl, String angle, Instant takenAt, String notes,
                          Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.photoUrl = photoUrl;
        this.angle = angle;
        this.takenAt = takenAt;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public static ProgressPhoto create(Long userId, String photoUrl, String angle, Instant takenAt,
                                       String notes) {
        return new ProgressPhoto(null, userId, photoUrl, angle, takenAt == null ? Instant.now() : takenAt,
                notes, Instant.now());
    }

    public static ProgressPhoto restore(Long id, Long userId, String photoUrl, String angle, Instant takenAt,
                                        String notes, Instant createdAt) {
        return new ProgressPhoto(id, userId, photoUrl, angle, takenAt, notes, createdAt);
    }

    public boolean belongsTo(Long userId) {
        return this.userId.equals(userId);
    }
}
