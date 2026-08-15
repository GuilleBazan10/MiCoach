package com.micoach.progress.presentation;

import com.micoach.shared.security.AuthenticatedUser;
import com.micoach.progress.application.port.in.ProgressUseCase;
import com.micoach.progress.application.port.in.ProgressUseCase.EntryData;
import com.micoach.progress.application.port.in.ProgressUseCase.PhotoData;
import com.micoach.progress.presentation.ProgressDtos.EntryRequest;
import com.micoach.progress.presentation.ProgressDtos.EntryResponse;
import com.micoach.progress.presentation.ProgressDtos.PhotoRequest;
import com.micoach.progress.presentation.ProgressDtos.PhotoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Contratos REST del módulo progress (base path /api/v1/progress). Todos requieren JWT
 * (configurado en app/security).
 */
@RestController
@RequestMapping("/api/v1/progress")
public class ProgressController {

    private final ProgressUseCase useCase;

    public ProgressController(ProgressUseCase useCase) {
        this.useCase = useCase;
    }

    // ------------------------- Métricas -------------------------

    @GetMapping("/entries")
    public List<EntryResponse> listEntries(@AuthenticationPrincipal AuthenticatedUser user,
                                           @RequestParam(required = false) String metricType) {
        return useCase.listEntries(user.id(), metricType).stream().map(EntryResponse::from).toList();
    }

    @PostMapping("/entries")
    @ResponseStatus(HttpStatus.CREATED)
    public EntryResponse addEntry(@AuthenticationPrincipal AuthenticatedUser user,
                                  @Valid @RequestBody EntryRequest request) {
        EntryData data = new EntryData(request.metricType(), request.value(), request.unit(),
                request.measuredAt(), request.notes());
        return EntryResponse.from(useCase.addEntry(user.id(), data));
    }

    @DeleteMapping("/entries/{entryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEntry(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long entryId) {
        useCase.deleteEntry(user.id(), entryId);
    }

    // ------------------------- Fotos -------------------------

    @GetMapping("/photos")
    public List<PhotoResponse> listPhotos(@AuthenticationPrincipal AuthenticatedUser user) {
        return useCase.listPhotos(user.id()).stream().map(PhotoResponse::from).toList();
    }

    @PostMapping("/photos")
    @ResponseStatus(HttpStatus.CREATED)
    public PhotoResponse addPhoto(@AuthenticationPrincipal AuthenticatedUser user,
                                  @Valid @RequestBody PhotoRequest request) {
        PhotoData data = new PhotoData(request.photoUrl(), request.angle(), request.takenAt(), request.notes());
        return PhotoResponse.from(useCase.addPhoto(user.id(), data));
    }

    @DeleteMapping("/photos/{photoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePhoto(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long photoId) {
        useCase.deletePhoto(user.id(), photoId);
    }
}
