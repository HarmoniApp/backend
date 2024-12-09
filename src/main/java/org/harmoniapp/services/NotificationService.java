package org.harmoniapp.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.NotificationDto;
import org.harmoniapp.entities.Notification;
import org.harmoniapp.entities.NotificationType;
import org.harmoniapp.entities.User;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing notifications.
 * Provides methods to perform CRUD operations on notifications.
 */
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final RepositoryCollector repositoryCollector;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Retrieves all notifications by user ID.
     *
     * @param userId the ID of the user for whom notifications are retrieved
     * @return a list of NotificationDto objects representing all notifications for the user
     */
    public List<NotificationDto> getAllNotificationsByUserId(Long userId) {
        List<Notification> notifications = repositoryCollector.getNotifications().findAllByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(NotificationDto::fromEntity)
                .toList();
    }

    /**
     * Retrieves all unread notifications by user ID.
     *
     * @param userId the ID of the user for whom unread notifications are retrieved
     * @return a list of NotificationDto objects representing unread notifications for the user
     */
    public List<NotificationDto> getAllUnreadNotificationsByUserId(Long userId) {
        List<Notification> notifications = repositoryCollector.getNotifications().findAllUnreadByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(NotificationDto::fromEntity)
                .toList();
    }

    /**
     * Creates a new notification.
     *
     * @param notificationDto the notificationDto containing the notification details
     * @return the created NotificationDto object
     */
    @Transactional
    public NotificationDto createNotification(NotificationDto notificationDto) {
        User user = repositoryCollector.getUsers().findById(notificationDto.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        NotificationType type = repositoryCollector.getNotificationTypes().findByTypeName(notificationDto.typeName());

        Notification notification = notificationDto.toEntity(user, type);
        Notification savedNotification = repositoryCollector.getNotifications().save(notification);

        messagingTemplate.convertAndSend("/client/notifications/" + user.getId(), NotificationDto.fromEntity(savedNotification));

        return NotificationDto.fromEntity(savedNotification);
    }

    /**
     * Marks a specific notification as read.
     *
     * @param notificationId the ID of the notification to mark as read
     * @return the updated NotificationDto object representing the read notification
     */
    @Transactional
    public NotificationDto markNotificationAsRead(Long notificationId) {
        Notification notification = repositoryCollector.getNotifications().findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);
        Notification savedNotification = repositoryCollector.getNotifications().save(notification);
        return NotificationDto.fromEntity(savedNotification);
    }

    /**
     * Marks all unread notifications for a specific user as read.
     *
     * @param userId the ID of the user whose notifications will be marked as read
     * @return a list of NotificationDto objects representing the marked notifications
     */
    @Transactional
    public List<NotificationDto> markAllNotificationsAsRead(Long userId) {
        List<Notification> notifications = repositoryCollector.getNotifications().findAllUnreadByUserIdOrderByCreatedAtDesc(userId);
        notifications.forEach(notification -> notification.setRead(true));
        repositoryCollector.getNotifications().saveAll(notifications);

        return notifications.stream()
                .map(NotificationDto::fromEntity)
                .toList();
    }

    /**
     * Deletes a notification by its ID.
     *
     * @param notificationId the ID of the notification to be deleted
     */
    public void deleteNotification(Long notificationId) {
        repositoryCollector.getNotifications().deleteById(notificationId);
    }


}
