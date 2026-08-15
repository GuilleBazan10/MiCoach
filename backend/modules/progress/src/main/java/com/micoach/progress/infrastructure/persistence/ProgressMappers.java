package com.micoach.progress.infrastructure.persistence;

import com.micoach.progress.domain.ProgressEntry;
import com.micoach.progress.domain.ProgressPhoto;

final class ProgressEntryMapper {

    private ProgressEntryMapper() {
    }

    static ProgressEntry toDomain(ProgressEntryJpa jpa) {
        return ProgressEntry.restore(jpa.getId(), jpa.getUserId(), jpa.getMetricType(), jpa.getValue(),
                jpa.getUnit(), jpa.getMeasuredAt(), jpa.getNotes(), jpa.getCreatedAt());
    }

    static ProgressEntryJpa toJpa(ProgressEntry domain) {
        return ProgressEntryJpa.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .metricType(domain.getMetricType())
                .value(domain.getValue())
                .unit(domain.getUnit())
                .measuredAt(domain.getMeasuredAt())
                .notes(domain.getNotes())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}

final class ProgressPhotoMapper {

    private ProgressPhotoMapper() {
    }

    static ProgressPhoto toDomain(ProgressPhotoJpa jpa) {
        return ProgressPhoto.restore(jpa.getId(), jpa.getUserId(), jpa.getPhotoUrl(), jpa.getAngle(),
                jpa.getTakenAt(), jpa.getNotes(), jpa.getCreatedAt());
    }

    static ProgressPhotoJpa toJpa(ProgressPhoto domain) {
        return ProgressPhotoJpa.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .photoUrl(domain.getPhotoUrl())
                .angle(domain.getAngle())
                .takenAt(domain.getTakenAt())
                .notes(domain.getNotes())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
