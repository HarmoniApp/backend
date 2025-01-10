package org.harmoniapp.services.notification;

import org.harmoniapp.contracts.notification.NotificationDto;

import java.util.List;

/**
 * Service interface for managing notifications.
 */
public interface NotificationService {

    /**
     * Retrieves all notifications for a specific user.
     *
     * @param userId the ID of the user
     * @return a list of notifications
     */
    List<NotificationDto> getAllByUserId(long userId);

    /**
     * Retrieves all unread notifications for a specific user.
     *
     * @param userId the ID of the user
     * @return a list of unread notifications
     */
    List<NotificationDto> getAllUnreadByUserId(long userId);

    /**
     * Creates a new notification.
     *
     * @param notificationDto the notification data transfer object
     * @return the created notification
     */
    NotificationDto create(NotificationDto notificationDto);

    /**
     * Marks all notifications as read for a specific user.
     *
     * @param userId the ID of the user
     * @return a list of notifications that were marked as read
     */
    List<NotificationDto> markAllAsReadByUserId(long userId);

    /**
     * Deletes a notification by its ID.
     *
     * @param notificationId the ID of the notification
     */
    void deleteById(long notificationId);
}
