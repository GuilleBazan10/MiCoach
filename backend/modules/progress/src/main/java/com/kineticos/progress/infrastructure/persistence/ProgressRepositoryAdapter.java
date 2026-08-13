package com.kineticos.progress.infrastructure.persistence;

import com.kineticos.progress.application.port.out.ProgressRepository;
import com.kineticos.progress.domain.ProgressEntry;
import com.kineticos.progress.domain.ProgressPhoto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador JPA del puerto {@link ProgressRepository}.
 */
@Component
public class ProgressRepositoryAdapter implements ProgressRepository {

    private final ProgressEntryJpaRepository entryRepository;
    private final ProgressPhotoJpaRepository photoRepository;

    public ProgressRepositoryAdapter(ProgressEntryJpaRepository entryRepository,
                                     ProgressPhotoJpaRepository photoRepository) {
        this.entryRepository = entryRepository;
        this.photoRepository = photoRepository;
    }

    @Override
    public List<ProgressEntry> findEntries(Long userId, String metricType) {
        List<ProgressEntryJpa> entries = metricType != null
                ? entryRepository.findByUserIdAndMetricTypeOrderByMeasuredAtDesc(userId, metricType)
                : entryRepository.findByUserIdOrderByMeasuredAtDesc(userId);
        return entries.stream().map(ProgressEntryMapper::toDomain).toList();
    }

    @Override
    public Optional<ProgressEntry> findEntryById(Long entryId) {
        return entryRepository.findById(entryId).map(ProgressEntryMapper::toDomain);
    }

    @Override
    public ProgressEntry saveEntry(ProgressEntry entry) {
        return ProgressEntryMapper.toDomain(entryRepository.save(ProgressEntryMapper.toJpa(entry)));
    }

    @Override
    public void deleteEntry(Long entryId) {
        entryRepository.deleteById(entryId);
    }

    @Override
    public List<ProgressPhoto> findPhotos(Long userId) {
        return photoRepository.findByUserIdOrderByTakenAtDesc(userId).stream()
                .map(ProgressPhotoMapper::toDomain).toList();
    }

    @Override
    public Optional<ProgressPhoto> findPhotoById(Long photoId) {
        return photoRepository.findById(photoId).map(ProgressPhotoMapper::toDomain);
    }

    @Override
    public ProgressPhoto savePhoto(ProgressPhoto photo) {
        return ProgressPhotoMapper.toDomain(photoRepository.save(ProgressPhotoMapper.toJpa(photo)));
    }

    @Override
    public void deletePhoto(Long photoId) {
        photoRepository.deleteById(photoId);
    }
}
