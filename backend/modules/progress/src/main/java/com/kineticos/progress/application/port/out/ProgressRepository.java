package com.kineticos.progress.application.port.out;

import com.kineticos.progress.domain.ProgressEntry;
import com.kineticos.progress.domain.ProgressPhoto;
import java.util.List;
import java.util.Optional;

public interface ProgressRepository {
    List<ProgressEntry> findEntries(Long userId, String metricType);
    Optional<ProgressEntry> findEntryById(Long entryId);
    ProgressEntry saveEntry(ProgressEntry entry);
    void deleteEntry(Long entryId);
    List<ProgressPhoto> findPhotos(Long userId);
    Optional<ProgressPhoto> findPhotoById(Long photoId);
    ProgressPhoto savePhoto(ProgressPhoto photo);
    void deletePhoto(Long photoId);
}
