package org.harmoniapp.utils;

import org.harmoniapp.contracts.notification.NotificationDto;
import org.harmoniapp.entities.schedule.Shift;

import java.time.LocalDateTime;

/**
 * Utility class for managing shift notifications.
 */
public class ShiftNotificationManager {

    /**
     * Creates a notification data transfer object for a newly published shift.
     *
     * @param publishedShift the published shift
     * @return the notification data transfer object
     */
    public static NotificationDto createPublishNotificationDto(Shift publishedShift) {
        return new NotificationDto(
                0L, // id is set automatically by the database
                publishedShift.getUser().getId(),
                "New Shift Published",
                "New shift published. Shift " +
                        publishedShift.getStart() + " - " + publishedShift.getEnd() +
                        " is published. Please check your schedule.",
                false,
                LocalDateTime.now()
        );
    }

    /**
     * Creates a notification data transfer object for a deleted shift.
     *
     * @param shift the deleted shift
     * @return the notification data transfer object
     */
    public static NotificationDto createDeletedNotificationDto(Shift shift) {
        return new NotificationDto(
                0L, // id is set automatically by the database
                shift.getUser().getId(),
                "Shift Was Deleted",
                "Shift Was Deleted. Shift " +
                        shift.getStart() + " - " + shift.getEnd() +
                        " was deleted. Please check your schedule.",
                false,
                LocalDateTime.now()
        );
    }
}
