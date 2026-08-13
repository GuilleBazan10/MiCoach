package com.kineticos.notification.presentation;

import com.kineticos.shared.security.AuthenticatedUser;
import com.kineticos.notification.application.port.in.NotificationUseCase;
import com.kineticos.notification.application.port.in.NotificationUseCase.NotificationData;
import com.kineticos.notification.application.port.in.NotificationUseCase.PreferenceData;
import com.kineticos.notification.application.port.in.NotificationUseCase.ReminderData;
import com.kineticos.notification.presentation.NotificationDtos.NotificationRequest;
import com.kineticos.notification.presentation.NotificationDtos.NotificationResponse;
import com.kineticos.notification.presentation.NotificationDtos.PreferenceRequest;
import com.kineticos.notification.presentation.NotificationDtos.PreferenceResponse;
import com.kineticos.notification.presentation.NotificationDtos.ReminderRequest;
import com.kineticos.notification.presentation.NotificationDtos.ReminderResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Contratos REST del módulo notification (base path /api/v1/notifications). Todos
 * requieren JWT (configurado en app/security).
 */
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationUseCase useCase;

    public NotificationController(NotificationUseCase useCase) {
        this.useCase = useCase;
    }

    // ------------------------- Notificaciones -------------------------

    @GetMapping
    public List<NotificationResponse> listNotifications(@AuthenticationPrincipal AuthenticatedUser user,
                                                         @RequestParam(required = false) String status) {
        return useCase.listNotifications(user.id(), status).stream().map(NotificationResponse::from).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationResponse createNotification(@AuthenticationPrincipal AuthenticatedUser user,
                                                    @Valid @RequestBody NotificationRequest request) {
        NotificationData data = new NotificationData(request.type(), request.title(), request.body(),
                request.data(), request.channel(), request.scheduledAt());
        return NotificationResponse.from(useCase.createNotification(user.id(), data));
    }

    @PutMapping("/{notificationId}/read")
    public NotificationResponse markRead(@AuthenticationPrincipal AuthenticatedUser user,
                                         @PathVariable Long notificationId) {
        return NotificationResponse.from(useCase.markRead(user.id(), notificationId));
    }

    @DeleteMapping("/{notificationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNotification(@AuthenticationPrincipal AuthenticatedUser user,
                                   @PathVariable Long notificationId) {
        useCase.deleteNotification(user.id(), notificationId);
    }

    // ------------------------- Recordatorios -------------------------

    @GetMapping("/reminders")
    public List<ReminderResponse> listReminders(@AuthenticationPrincipal AuthenticatedUser user) {
        return useCase.listReminders(user.id()).stream().map(ReminderResponse::from).toList();
    }

    @PostMapping("/reminders")
    @ResponseStatus(HttpStatus.CREATED)
    public ReminderResponse createReminder(@AuthenticationPrincipal AuthenticatedUser user,
                                           @Valid @RequestBody ReminderRequest request) {
        return ReminderResponse.from(useCase.createReminder(user.id(), toReminderData(request)));
    }

    @PutMapping("/reminders/{reminderId}")
    public ReminderResponse updateReminder(@AuthenticationPrincipal AuthenticatedUser user,
                                           @PathVariable Long reminderId,
                                           @Valid @RequestBody ReminderRequest request) {
        return ReminderResponse.from(useCase.updateReminder(user.id(), reminderId, toReminderData(request)));
    }

    @DeleteMapping("/reminders/{reminderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReminder(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long reminderId) {
        useCase.deleteReminder(user.id(), reminderId);
    }

    private ReminderData toReminderData(ReminderRequest request) {
        return new ReminderData(request.reminderType(), request.scheduleCron(), request.scheduleConfig(),
                request.title(), request.body(), request.enabled());
    }

    // ------------------------- Preferencias -------------------------

    @GetMapping("/preferences")
    public List<PreferenceResponse> listPreferences(@AuthenticationPrincipal AuthenticatedUser user) {
        return useCase.listPreferences(user.id()).stream().map(PreferenceResponse::from).toList();
    }

    @PutMapping("/preferences")
    public PreferenceResponse setPreference(@AuthenticationPrincipal AuthenticatedUser user,
                                            @Valid @RequestBody PreferenceRequest request) {
        PreferenceData data = new PreferenceData(request.eventType(), request.channel(), request.enabled());
        return PreferenceResponse.from(useCase.setPreference(user.id(), data));
    }
}
