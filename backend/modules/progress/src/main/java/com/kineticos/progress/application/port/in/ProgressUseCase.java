package com.kineticos.progress.application.port.in;

import com.kineticos.progress.domain.ProgressEntry;
import com.kineticos.progress.domain.ProgressPhoto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Puerto de entrada del módulo progress (métricas de seguimiento y fotos).
 */
public interface ProgressUseCase {

    List<ProgressEntry> listEntries(Long userId, String metricType);

    ProgressEntry addEntry(Long userId, EntryData data);

    void deleteEntry(Long userId, Long entryId);

    List<ProgressPhoto> listPhotos(Long userId);

    ProgressPhoto addPhoto(Long userId, PhotoData data);

    void deletePhoto(Long userId, Long photoId);

    record EntryData(String metricType, BigDecimal value, String unit, Instant measuredAt, String notes) {
    }

    record PhotoData(String photoUrl, String angle, Instant takenAt, String notes) {
    }
}
