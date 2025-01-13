package org.harmoniapp.services.schedule;

import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.enums.ShiftNotificationType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Interface for sending shift notifications.
 */
public interface ShiftNotificationSender {

    /**
     * Sends notifications for a list of shifts.
     *
     * @param shifts the list of shifts to notify about
     * @param type the type of notification to send
     */
    @Transactional
    void send(List<Shift> shifts, ShiftNotificationType type);

    /**
     * Sends a notification for a single shift.
     *
     * @param shift the shift to notify about
     * @param type the type of notification to send
     */
    @Transactional
    void send(Shift shift, ShiftNotificationType type);
}
