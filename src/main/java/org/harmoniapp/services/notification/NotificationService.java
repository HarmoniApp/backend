package org.harmoniapp.services.notification;

import org.harmoniapp.contracts.notification.NotificationDto;

import java.util.List;

public interface NotificationService {

    List<NotificationDto> getAllByUserId(long userId);

    List<NotificationDto> getAllUnreadByUserId(long userId);

    NotificationDto create(NotificationDto notificationDto);

    NotificationDto markAsReadById(long notificationId);

    List<NotificationDto> markAllAsReadByUserId(long userId);

    void deleteById(long notificationId);
}
