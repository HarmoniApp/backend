package org.harmoniapp.services.schedule;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.notification.NotificationDto;
import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.enums.ShiftNotificationType;
import org.harmoniapp.services.notification.NotificationService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the ShiftNotificationSender interface.
 * This class is responsible for sending notifications related to shifts.
 */
@Service
@RequiredArgsConstructor
public class ShiftNotificationSenderImpl implements ShiftNotificationSender {
    private final NotificationService notificationService;

    /**
     * Sends notifications for a list of shifts asynchronously.
     *
     * @param shifts the list of Shift entities for which notifications are to be sent
     * @param type   the type of notification to be sent
     */
    @Override
    @Async
    public void send(List<Shift> shifts, ShiftNotificationType type) {
        shifts.forEach(shift -> send(shift, type));
    }

    /**
     * Sends a notification for the specified shift and notification type.
     *
     * @param shift the Shift entity for which the notification is to be sent
     * @param type  the type of notification to be sent
     */
    @Override
    @Async
    public void send(Shift shift, ShiftNotificationType type) {
        NotificationDto notificationDto = createNotification(shift, type);
        notificationService.create(notificationDto);
    }

    /**
     * Creates a NotificationDto for the specified shift and notification type.
     *
     * @param shift the Shift entity for which the notification is to be created
     * @param type  the type of notification to be created
     * @return the created NotificationDto
     * @throws IllegalArgumentException if the notification type is unknown
     */
    private static NotificationDto createNotification(Shift shift, ShiftNotificationType type) {
        long receiverId = shift.getUser().getId();
        switch (type) {
            case PUBLISHED_SHIFT, DELETED_SHIFT -> {
                return createNotification(receiverId, type.getTitle(), type.formatMessage(shift.getStart(), shift.getEnd()));
            }
            default -> throw new IllegalArgumentException("Unknown notification type");
        }
    }

    /**
     * Creates a NotificationDto with the specified receiver ID, title, and message.
     *
     * @param receiverId the ID of the receiver
     * @param title      the title of the notification
     * @param message    the message of the notification
     * @return the created NotificationDto
     */
    private static NotificationDto createNotification(long receiverId, String title, String message) {
        return NotificationDto.createNotification(receiverId, title, message);
    }
}
