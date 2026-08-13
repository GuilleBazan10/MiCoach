package com.kineticos.progress.application.service;

import com.kineticos.shared.error.DomainException;
import com.kineticos.shared.error.ErrorCode;
import com.kineticos.progress.application.port.in.ProgressUseCase;
import com.kineticos.progress.application.port.out.ProgressRepository;
import com.kineticos.progress.domain.ProgressEntry;
import com.kineticos.progress.domain.ProgressPhoto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación de casos de uso del módulo progress. Depende solo del puerto de salida.
 */
@Service
public class ProgressService implements ProgressUseCase {

    private final ProgressRepository repository;

    public ProgressService(ProgressRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgressEntry> listEntries(Long userId, String metricType) {
        return repository.findEntries(userId, metricType);
    }

    @Override
    @Transactional
    public ProgressEntry addEntry(Long userId, EntryData data) {
        return repository.saveEntry(ProgressEntry.create(userId, data.metricType(), data.value(), data.unit(),
                data.measuredAt(), data.notes()));
    }

    @Override
    @Transactional
    public void deleteEntry(Long userId, Long entryId) {
        ProgressEntry entry = repository.findEntryById(entryId)
                .orElseThrow(() -> new DomainException(404, ErrorCode.NOT_FOUND, "Registro no encontrado"));
        if (!entry.belongsTo(userId)) {
            throw new DomainException(404, ErrorCode.NOT_FOUND, "Registro no encontrado");
        }
        repository.deleteEntry(entryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgressPhoto> listPhotos(Long userId) {
        return repository.findPhotos(userId);
    }

    @Override
    @Transactional
    public ProgressPhoto addPhoto(Long userId, PhotoData data) {
        return repository.savePhoto(ProgressPhoto.create(userId, data.photoUrl(), data.angle(), data.takenAt(),
                data.notes()));
    }

    @Override
    @Transactional
    public void deletePhoto(Long userId, Long photoId) {
        ProgressPhoto photo = repository.findPhotoById(photoId)
                .orElseThrow(() -> new DomainException(404, ErrorCode.NOT_FOUND, "Foto no encontrada"));
        if (!photo.belongsTo(userId)) {
            throw new DomainException(404, ErrorCode.NOT_FOUND, "Foto no encontrada");
        }
        repository.deletePhoto(photoId);
    }
}
