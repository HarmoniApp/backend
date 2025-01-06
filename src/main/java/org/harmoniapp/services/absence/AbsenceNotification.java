package org.harmoniapp.services.absence;

import org.harmoniapp.contracts.notification.NotificationDto;
import org.harmoniapp.entities.absence.Absence;
import org.harmoniapp.enums.AbsenceNotificationType;

/**
 * This class is responsible for creating absence notifications.
 */
public class AbsenceNotification {

    /**
     * Creates a notification based on the given absence and notification type.
     *
     * @param savedAbsence the saved absence entity
     * @param type         the type of absence notification
     * @return a NotificationDto object containing the notification details
     */
    public static NotificationDto createNotification(Absence savedAbsence, AbsenceNotificationType type) {
        long receiverId = getReceiverId(savedAbsence, type);
        String message = getMessage(savedAbsence, type);
        return createNotification(receiverId, type.getTitle(), message);
    }

    /**
     * Determines the receiver ID based on the absence and notification type.
     *
     * @param absence the absence entity
     * @param type    the type of absence notification
     * @return the ID of the notification receiver
     * @throws IllegalArgumentException if the notification type is unknown
     */
    private static long getReceiverId(Absence absence, AbsenceNotificationType type) {
        switch (type) {
            case NEW_ABSENCE, EMPLOYEE_DELETED -> {
                return absence.getUser().getSupervisor().getId();
            }
            case EMPLOYER_UPDATED -> {
                return absence.getUser().getId();
            }
            default -> throw new IllegalArgumentException("Unknown notification type");
        }
    }

    /**
     * Generates a message for the notification based on the absence and notification type.
     *
     * @param absence the absence entity
     * @param type    the type of absence notification
     * @return the message content of the notification
     * @throws IllegalArgumentException if the notification type is unknown
     */
    private static String getMessage(Absence absence, AbsenceNotificationType type) {
        switch (type) {
            case NEW_ABSENCE, EMPLOYEE_DELETED -> {
                return type.formatMessage(absence.getUser().getFirstname(), absence.getUser().getSurname());
            }
            case EMPLOYER_UPDATED -> {
                return type.formatMessage(absence.getStart(), absence.getEnd(), absence.getStatus().getName());
            }
            default -> throw new IllegalArgumentException("Unknown notification type");
        }
    }

    /**
     * Helper method to create a NotificationDto object.
     *
     * @param receiverId the ID of the notification receiver
     * @param title      the title of the notification
     * @param message    the message content of the notification
     * @return a NotificationDto object containing the notification details
     */
    private static NotificationDto createNotification(long receiverId, String title, String message) {
        return NotificationDto.createNotification(receiverId, title, message);
    }
}
