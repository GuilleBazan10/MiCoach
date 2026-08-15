package com.micoach.progress.presentation;

import com.micoach.progress.domain.ProgressEntry;
import com.micoach.progress.domain.ProgressPhoto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTOs del módulo progress. Cada verbose class es un contrato de entrada/salida.
 */
public final class ProgressDtos {

    private ProgressDtos() {
    }

    public record EntryResponse(Long id, String metricType, BigDecimal value, String unit, Instant measuredAt,
                                String notes) {

        static EntryResponse from(ProgressEntry e) {
            return new EntryResponse(e.getId(), e.getMetricType(), e.getValue(), e.getUnit(), e.getMeasuredAt(),
                    e.getNotes());
        }
    }

    public record EntryRequest(@NotBlank String metricType, @NotNull BigDecimal value, @NotBlank String unit,
                               Instant measuredAt, @Size(max = 500) String notes) {
    }

    public record PhotoResponse(Long id, String photoUrl, String angle, Instant takenAt, String notes) {

        static PhotoResponse from(ProgressPhoto p) {
            return new PhotoResponse(p.getId(), p.getPhotoUrl(), p.getAngle(), p.getTakenAt(), p.getNotes());
        }
    }

    public record PhotoRequest(@NotBlank @Size(max = 500) String photoUrl, String angle, Instant takenAt,
                               @Size(max = 500) String notes) {
    }
}
