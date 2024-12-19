package org.harmoniapp.controllers.notification;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.notification.NotificationDto;
import org.harmoniapp.services.notification.NotificationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing notifications.
 * Provides endpoints to perform CRUD operations on notifications.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("notification")
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
        return notificationService.getAllByUserId(id);
    }

    /**
     * Retrieves all unread notifications for a specific user.
     *
     * @param id the ID of the user whose unread notifications are being retrieved
     * @return a list of NotificationDto objects representing all unread notifications for the user
     */
    @GetMapping("/user/{id}/unread")
    public List<NotificationDto> getAllUnreadNotificationsByUserId(@PathVariable Long id) {
        return notificationService.getAllUnreadByUserId(id);
    }

    /**
     * Marks all unread notifications for a specific user as read.
     *
     * @param id the ID of the user whose unread notifications will be marked as read
     * @return a list of NotificationDto objects representing the marked notifications
     */
    @PatchMapping("/user/{id}/read")
    public List<NotificationDto> markAllNotificationsAsRead(@PathVariable Long id) {
        return notificationService.markAllAsReadByUserId(id);
    }

    /**
     * Deletes a notification by its ID.
     *
     * <p>This endpoint is protected by security measures, and will only allow the owner of the notification
     *  to delete it.</p>
     *
     * @param id the ID of the notification to delete
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@securityService.isNotificationOwner(#id, authentication)")
    public void deleteNotification(@PathVariable Long id) {
        notificationService.deleteById(id);
    }
}
