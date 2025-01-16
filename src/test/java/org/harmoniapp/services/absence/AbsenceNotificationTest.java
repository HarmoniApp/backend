package org.harmoniapp.services.absence;

import org.harmoniapp.contracts.notification.NotificationDto;
import org.harmoniapp.entities.absence.Absence;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.enums.AbsenceNotificationType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AbsenceNotificationTest {

    @Test
    public void createNotificationTest() {
        Absence absence = mock(Absence.class);
        User user = mock(User.class);
        when(absence.getUser()).thenReturn(user);
        when(user.getSupervisor()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(user.getFirstname()).thenReturn("John");
        when(user.getSurname()).thenReturn("Doe");
        AbsenceNotificationType type = AbsenceNotificationType.NEW_ABSENCE;

        NotificationDto result = AbsenceNotification.createNotification(absence, type);

        assertNotNull(result);
        assertEquals(1L, result.userId());
        assertEquals(type.getTitle(), result.title());
        assertEquals(type.formatMessage("John", "Doe"), result.message());
    }
}