package org.harmoniapp.services.schedule;

import org.harmoniapp.contracts.notification.NotificationDto;
import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.enums.ShiftNotificationType;
import org.harmoniapp.services.notification.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShiftNotificationSenderImplTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ShiftNotificationSenderImpl shiftNotificationSender;

    @Test
    public void sendTest() {
        User user = User.builder().id(1L).build();
        Shift shift = Shift.builder()
                .user(user)
                .build();
        List<Shift> shifts = List.of(shift);

        shiftNotificationSender.send(shifts, ShiftNotificationType.PUBLISHED_SHIFT);

        verify(notificationService, times(1)).create(any(NotificationDto.class));
    }

    @Test
    public void sendSingleShiftTest() {
        User user = User.builder().id(1L).build();
        Shift shift = Shift.builder()
                .user(user)
                .build();

        shiftNotificationSender.send(shift, ShiftNotificationType.PUBLISHED_SHIFT);

        verify(notificationService, times(1)).create(any(NotificationDto.class));
    }
}