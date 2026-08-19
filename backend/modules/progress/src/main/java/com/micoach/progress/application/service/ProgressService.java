package com.micoach.progress.application.service;

import com.micoach.shared.error.DomainException;
import com.micoach.shared.error.ErrorCode;
import com.micoach.shared.event.AuditLogEvent;
import com.micoach.progress.application.port.in.ProgressUseCase;
import com.micoach.progress.application.port.out.ProgressRepository;
import com.micoach.progress.domain.ProgressEntry;
import com.micoach.progress.domain.ProgressPhoto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación de casos de uso del módulo progress. Depende solo del puerto de salida.
 */
@Slf4j
@Service
public class ProgressService implements ProgressUseCase {

    private final ProgressRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public ProgressService(ProgressRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgressEntry> listEntries(Long userId, String metricType) {
        return repository.findEntries(userId, metricType);
    }

    @Override
    @Transactional
    public ProgressEntry addEntry(Long userId, EntryData data) {
        log.info("Añadiendo registro de progreso físico para el usuario ID: {} (Métrica: {}, Valor: {})", 
                userId, data.metricType(), data.value());
        ProgressEntry entry = ProgressEntry.create(userId, data.metricType(), data.value(), data.unit(),
                data.measuredAt(), data.notes());
        ProgressEntry saved = repository.saveEntry(entry);
        
        log.info("Registro de progreso físico añadido exitosamente con ID: {} para el usuario ID: {}", saved.getId(), userId);
        eventPublisher.publishEvent(AuditLogEvent.of(userId, "PROGRESS_ENTRY_ADD", "PROGRESS_ENTRY", saved.getId()));
        return saved;
    }

    @Override
    @Transactional
    public void deleteEntry(Long userId, Long entryId) {
        log.info("Eliminando registro de progreso físico ID: {} para el usuario ID: {}", entryId, userId);
        ProgressEntry entry = repository.findEntryById(entryId)
                .orElseThrow(() -> {
                    log.warn("Eliminación fallida: registro de progreso ID {} no encontrado para el usuario ID: {}", entryId, userId);
                    return new DomainException(404, ErrorCode.NOT_FOUND, "Registro no encontrado");
                });
        if (!entry.belongsTo(userId)) {
            log.warn("Eliminación fallida: el registro de progreso ID {} no pertenece al usuario ID: {}", entryId, userId);
            throw new DomainException(404, ErrorCode.NOT_FOUND, "Registro no encontrado");
        }
        repository.deleteEntry(entryId);
        
        log.info("Registro de progreso físico ID: {} eliminado exitosamente para el usuario ID: {}", entryId, userId);
        eventPublisher.publishEvent(AuditLogEvent.of(userId, "PROGRESS_ENTRY_DELETE", "PROGRESS_ENTRY", entryId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgressPhoto> listPhotos(Long userId) {
        return repository.findPhotos(userId);
    }

    @Override
    @Transactional
    public ProgressPhoto addPhoto(Long userId, PhotoData data) {
        log.info("Añadiendo foto de progreso físico para el usuario ID: {} (Ángulo: {})", userId, data.angle());
        ProgressPhoto photo = ProgressPhoto.create(userId, data.photoUrl(), data.angle(), data.takenAt(),
                data.notes());
        ProgressPhoto saved = repository.savePhoto(photo);
        
        log.info("Foto de progreso físico añadida exitosamente con ID: {} para el usuario ID: {}", saved.getId(), userId);
        eventPublisher.publishEvent(AuditLogEvent.of(userId, "PROGRESS_PHOTO_ADD", "PROGRESS_PHOTO", saved.getId()));
        return saved;
    }

    @Override
    @Transactional
    public void deletePhoto(Long userId, Long photoId) {
        log.info("Eliminando foto de progreso físico ID: {} para el usuario ID: {}", photoId, userId);
        ProgressPhoto photo = repository.findPhotoById(photoId)
                .orElseThrow(() -> {
                    log.warn("Eliminación fallida: foto de progreso ID {} no encontrada para el usuario ID: {}", photoId, userId);
                    return new DomainException(404, ErrorCode.NOT_FOUND, "Foto no encontrada");
                });
        if (!photo.belongsTo(userId)) {
            log.warn("Eliminación fallida: la foto de progreso ID {} no pertenece al usuario ID: {}", photoId, userId);
            throw new DomainException(404, ErrorCode.NOT_FOUND, "Foto no encontrada");
        }
        repository.deletePhoto(photoId);
        
        log.info("Foto de progreso físico ID: {} eliminada exitosamente para el usuario ID: {}", photoId, userId);
        eventPublisher.publishEvent(AuditLogEvent.of(userId, "PROGRESS_PHOTO_DELETE", "PROGRESS_PHOTO", photoId));
    }
}
