package com.micoach.progress.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Entidad JPA de la tabla {@code progress_photos}.
 */
@Entity
@Table(name = "progress_photos")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressPhotoJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "photo_url", nullable = false)
    private String photoUrl;

    @Column(name = "angle")
    private String angle;

    @Column(name = "taken_at", nullable = false)
    private Instant takenAt;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
