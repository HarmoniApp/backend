package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.NotificationDto;
import org.harmoniapp.harmoniwebapi.services.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing notifications.
 * Provides endpoints to perform CRUD operations on notifications.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("notification")
@CrossOrigin(origins = "http://localhost:3000")
public class NotificationController {
    private final NotificationService notificationService;

    /**
     * Retrieves all notifications for a specific user.
     *
     * @param id the ID of the user whose notifications are being retrieved
     * @return a list of NotificationDto objects representing all notifications for the user
     */
    @GetMapping("/user/{id}")
    public List<NotificationDto> getAllNotificationsByUserId(@PathVariable Long id) {
        return notificationService.getAllNotificationsByUserId(id);
    }

    /**
     * Retrieves all unread notifications for a specific user.
     *
     * @param id the ID of the user whose unread notifications are being retrieved
     * @return a list of NotificationDto objects representing all unread notifications for the user
     */
    @GetMapping("/user/{id}/unread")
    public List<NotificationDto> getAllUnreadNotificationsByUserId(@PathVariable Long id) {
        return notificationService.getAllUnreadNotificationsByUserId(id);
    }

    /**
     * Marks a notification as read.
     *
     * @param id the ID of the notification to mark as read
     * @return the updated NotificationDto object representing the read notification
     */
    @PatchMapping("/{id}/read")
    public NotificationDto markNotificationAsRead(@PathVariable Long id) {
        return notificationService.markNotificationAsRead(id);
    }

    /**
     * Marks all unread notifications for a specific user as read.
     *
     * @param id the ID of the user whose unread notifications will be marked as read
     * @return a list of NotificationDto objects representing the marked notifications
     */
    @PatchMapping("/user/{id}/read")
    public List<NotificationDto> markAllNotificationsAsRead(@PathVariable Long id) {
        return notificationService.markAllNotificationsAsRead(id);
    }

    /**
     * Deletes a notification by its ID.
     *
     * @param id the ID of the notification to delete
     */
    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
    }
}
