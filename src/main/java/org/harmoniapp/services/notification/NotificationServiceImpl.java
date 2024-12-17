package org.harmoniapp.services.notification;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.notification.NotificationDto;
import org.harmoniapp.entities.notification.Notification;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.exception.EntityNotFound;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for managing notifications.
 */
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final RepositoryCollector repositoryCollector;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Retrieves all notifications for a specific user, ordered by creation date in descending order.
     *
     * @param userId the ID of the user whose notifications are to be retrieved
     * @return a list of NotificationDto objects representing the user's notifications
     * @throws EntityNotFound if the user with the specified ID does not exist
     */
    @Override
    public List<NotificationDto> getAllByUserId(long userId) {
        verifyUserExist(userId);
        return repositoryCollector.getNotifications()
                .findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationDto::fromEntity)
                .toList();
    }

    /**
     * Verifies if a user with the given ID exists.
     *
     * @param userId the ID of the user to verify
     * @throws EntityNotFound if the user with the specified ID does not exist
     */
    private void verifyUserExist(long userId) {
        if (!repositoryCollector.getUsers().existsById(userId)) {
            throw new EntityNotFound("Nie znaleziono użytkownika o podanym ID: " + userId);
        }
    }

    /**
     * Retrieves all unread notifications for a specific user, ordered by creation date in descending order.
     *
     * @param userId the ID of the user whose unread notifications are to be retrieved
     * @return a list of NotificationDto objects representing the user's unread notifications
     * @throws EntityNotFound if the user with the specified ID does not exist
     */
    @Override
    public List<NotificationDto> getAllUnreadByUserId(long userId) {
        verifyUserExist(userId);
        return repositoryCollector.getNotifications()
                .findAllUnreadByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationDto::fromEntity)
                .toList();
    }

    /**
     * Creates a new notification for a user.
     *
     * @param notificationDto the data transfer object containing the notification details
     * @return a NotificationDto object representing the created notification
     */
    @Override
    @Transactional
    public NotificationDto create(NotificationDto notificationDto) {
        User user = getUserById(notificationDto.userId());
        Notification notification = notificationDto.toEntity(user);
        Notification savedNotification = repositoryCollector.getNotifications().save(notification);
        return notificationCreateResponse(savedNotification);
    }

    /**
     * Converts a Notification entity to a NotificationDto and sends it to the client.
     *
     * @param notification the Notification entity to be converted and sent
     * @return a NotificationDto object representing the converted notification
     */
    private NotificationDto notificationCreateResponse(Notification notification) {
        NotificationDto notificationDto = NotificationDto.fromEntity(notification);
        messagingTemplate.convertAndSend("/client/notifications/" + notification.getUser().getId(), notificationDto);
        return notificationDto;
    }

    /**
     * Retrieves a User entity by its ID.
     *
     * @param userId the ID of the user to retrieve
     * @return the User entity with the specified ID
     * @throws EntityNotFound if the user with the specified ID does not exist
     */
    private User getUserById(long userId) {
        return repositoryCollector.getUsers().findById(userId)
                .orElseThrow(() -> new EntityNotFound("Nie znaleziono użytkownika o podanym ID: " + userId));
    }

    /**
     * Marks a notification as read by its ID.
     *
     * @param notificationId the ID of the notification to mark as read
     * @return a NotificationDto object representing the marked notification
     * @throws EntityNotFound if the notification with the specified ID does not exist
     */
    @Override
    @Transactional
    public NotificationDto markAsReadById(long notificationId) {
        Notification notification = repositoryCollector.getNotifications().findById(notificationId)
                .orElseThrow(() -> new EntityNotFound("Nie znaleziono powiadomienia o podanym ID: " + notificationId));
        return markAsRead(notification);
    }

    /**
     * Marks all unread notifications as read for a specific user.
     *
     * @param userId the ID of the user whose unread notifications are to be marked as read
     * @return a list of NotificationDto objects representing the marked notifications
     * @throws EntityNotFound if the user with the specified ID does not exist
     */
    @Transactional
    public List<NotificationDto> markAllAsReadByUserId(long userId) {
        List<Notification> notifications = repositoryCollector.getNotifications().findAllUnreadByUserIdOrderByCreatedAtDesc(userId);
        return markAsRead(notifications);
    }

    /**
     * Marks a single notification as read.
     *
     * @param notification the Notification entity to be marked as read
     * @return a NotificationDto object representing the marked notification
     */
    private NotificationDto markAsRead(Notification notification) {
        List<NotificationDto> notificationDtoList = markAsReadNotifications(List.of(notification));
        assert notificationDtoList != null;
        return notificationDtoList.getFirst();
    }

    /**
     * Marks a list of notifications as read.
     *
     * @param notifications the list of Notification entities to be marked as read
     * @return a list of NotificationDto objects representing the marked notifications
     */
    private List<NotificationDto> markAsRead(List<Notification> notifications) {
        return markAsReadNotifications(notifications);
    }

    /**
     * Marks a list of notifications as read.
     *
     * @param notifications the list of Notification entities to be marked as read
     * @return a list of NotificationDto objects representing the marked notifications
     */
    private List<NotificationDto> markAsReadNotifications(List<Notification> notifications) {
        notifications.forEach(notification -> notification.setRead(true));
        notifications = repositoryCollector.getNotifications().saveAll(notifications);
        return notifications.stream()
                .map(NotificationDto::fromEntity)
                .toList();
    }

    /**
     * Deletes a notification by its ID.
     *
     * @param notificationId the ID of the notification to delete
     */
    public void deleteById(long notificationId) {
        repositoryCollector.getNotifications().deleteById(notificationId);
    }
}