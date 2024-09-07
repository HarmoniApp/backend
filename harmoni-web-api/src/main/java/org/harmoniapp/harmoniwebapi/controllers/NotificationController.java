package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.NotificationDto;
import org.harmoniapp.harmoniwebapi.services.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("notification")
@CrossOrigin(origins = "http://localhost:3000")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/user/{id}")
    public List<NotificationDto> getAllNotificationsByUserId(@PathVariable Long id) {
        return notificationService.getAllNotificationsByUserId(id);
    }

    @GetMapping("/user/{id}/unread")
    public List<NotificationDto> getAllUnreadNotificationsByUserId(@PathVariable Long id) {
        return notificationService.getAllUnreadNotificationsByUserId(id);
    }

    @PatchMapping("/{id}/read")
    public NotificationDto markNotificationAsRead(@PathVariable Long id) {
        return notificationService.markNotificationAsRead(id);
    }

    @PatchMapping("/user/{id}/read")
    public List<NotificationDto> markAllNotificationsAsRead(@PathVariable Long id) {
        return notificationService.markAllNotificationsAsRead(id);
    }

    @DeleteMapping("{id}")
    public void deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
    }
}
