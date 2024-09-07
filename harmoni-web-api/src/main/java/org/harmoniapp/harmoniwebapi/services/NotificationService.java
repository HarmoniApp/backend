package org.harmoniapp.harmoniwebapi.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.Notification;
import org.harmoniapp.harmonidata.entities.NotificationType;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.NotificationDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@ComponentScan(basePackages = {"org.harmoniapp.harmonidata"})
public class NotificationService {
    private final RepositoryCollector repositoryCollector;

    public List<NotificationDto> getAllNotificationsByUserId(Long userId) {
        List<Notification> notifications = repositoryCollector.getNotifications().findAllByUserId(userId);
        return notifications.stream()
                .map(NotificationDto::fromEntity)
                .toList();
    }

    public List<NotificationDto> getAllUnreadNotificationsByUserId(Long userId) {
        List<Notification> notifications = repositoryCollector.getNotifications().findAllUnreadByUserId(userId);
        return notifications.stream()
                .map(NotificationDto::fromEntity)
                .toList();
    }

    @Transactional
    public NotificationDto createNotification(NotificationDto notificationDto) {
        User user = repositoryCollector.getUsers().findById(notificationDto.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        NotificationType type = repositoryCollector.getNotificationTypes().findByTypeName(notificationDto.typeName());


        Notification notification = notificationDto.toEntity(user, type);
        notification.setCreatedAt(LocalDateTime.now());
        Notification savedNotification = repositoryCollector.getNotifications().save(notification);
        return NotificationDto.fromEntity(savedNotification);
    }

    @Transactional
    public NotificationDto markNotificationAsRead(Long notificationId) {
        Notification notification = repositoryCollector.getNotifications().findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);
        Notification savedNotification = repositoryCollector.getNotifications().save(notification);
        return NotificationDto.fromEntity(savedNotification);
    }

    @Transactional
    public List<NotificationDto> markAllNotificationsAsRead(Long userId) {
        List<Notification> notifications = repositoryCollector.getNotifications().findAllUnreadByUserId(userId);
        notifications.forEach(notification -> notification.setRead(true));
        repositoryCollector.getNotifications().saveAll(notifications);

        return notifications.stream()
                .map(NotificationDto::fromEntity)
                .toList();
    }

    public void deleteNotification(Long notificationId) {
        repositoryCollector.getNotifications().deleteById(notificationId);
    }


}
