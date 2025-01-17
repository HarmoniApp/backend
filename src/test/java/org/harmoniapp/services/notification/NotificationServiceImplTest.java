package org.harmoniapp.services.notification;

import org.harmoniapp.contracts.notification.NotificationDto;
import org.harmoniapp.entities.notification.Notification;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.exception.EntityNotFoundException;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.repositories.notification.NotificationRepository;
import org.harmoniapp.repositories.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {

    @Mock
    private RepositoryCollector repositoryCollector;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    public void getAllByUserIdTest() {
        long userId = 1L;
        User user = User.builder().id(1L).build();
        Notification notification = Notification.builder().id(1L).user(user).read(true).build();
        NotificationDto notificationDto = NotificationDto.builder().id(1L).userId(1L).build();
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(repositoryCollector.getNotifications()).thenReturn(notificationRepository);
        when(notificationRepository.findAllByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(notification));
        try (MockedStatic<NotificationDto> mockedStatic = mockStatic(NotificationDto.class)) {
            mockedStatic.when(() -> NotificationDto.fromEntity(notification)).thenReturn(notificationDto);
        }

        List<NotificationDto> result = notificationService.getAllByUserId(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void getAllByUserIdNotFoundTest() {
        long userId = 1L;
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> notificationService.getAllByUserId(userId));
    }

    @Test
    public void getAllUnreadByUserIdTest() {
        long userId = 1L;
        User user = User.builder().id(1L).build();
        Notification notification = Notification.builder().id(1L).user(user).read(false).build();
        NotificationDto notificationDto = NotificationDto.builder().id(1L).userId(1L).build();
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(repositoryCollector.getNotifications()).thenReturn(notificationRepository);
        when(notificationRepository.findAllUnreadByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(notification));

        List<NotificationDto> result;
        try (MockedStatic<NotificationDto> mockedStatic = mockStatic(NotificationDto.class)) {
            mockedStatic.when(() -> NotificationDto.fromEntity(notification)).thenReturn(notificationDto);
            result = notificationService.getAllUnreadByUserId(userId);
        }

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void getAllUnreadByUserIdNotFoundTest() {
        long userId = 1L;
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> notificationService.getAllUnreadByUserId(userId));
    }

    @Test
    public void createTest() {
        NotificationDto notificationDto = NotificationDto.builder().id(1L).userId(1L).build();
        User user = User.builder().id(1L).build();
        Notification notification = Notification.builder().id(1L).user(user).read(false).build();
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(repositoryCollector.getNotifications()).thenReturn(notificationRepository);
        when(notificationRepository.save(notification)).thenReturn(notification);

        NotificationDto result;
        try (MockedStatic<NotificationDto> mockedStatic = mockStatic(NotificationDto.class)) {
            mockedStatic.when(() -> NotificationDto.fromEntity(notification)).thenReturn(notificationDto);
            result = notificationService.create(notificationDto);
        }

        assertNotNull(result);
    }

    @Test
    public void createUserNotFoundTest() {
        NotificationDto notificationDto = NotificationDto.builder().id(1L).userId(1L).build();
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> notificationService.create(notificationDto));
    }

    @Test
    public void markAllAsReadByUserIdTest() {
        long userId = 1L;
        User user = User.builder().id(1L).build();
        Notification notification = Notification.builder().id(1L).user(user).read(false).build();
        NotificationDto notificationDto = NotificationDto.builder().id(1L).userId(1L).read(true).build();
        when(repositoryCollector.getNotifications()).thenReturn(notificationRepository);
        when(notificationRepository.findAllUnreadByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(notification));
        when(notificationRepository.saveAll(List.of(notification))).thenReturn(List.of(notification));

        List<NotificationDto> result;
        try (MockedStatic<NotificationDto> mockedStatic = mockStatic(NotificationDto.class)) {
            mockedStatic.when(() -> NotificationDto.fromEntity(notification)).thenReturn(notificationDto);
            result = notificationService.markAllAsReadByUserId(userId);
        }

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void deleteByIdTest() {
        long notificationId = 1L;
        when(repositoryCollector.getNotifications()).thenReturn(notificationRepository);
        doNothing().when(notificationRepository).deleteById(notificationId);

        notificationService.deleteById(notificationId);

        verify(repositoryCollector.getNotifications(), times(1)).deleteById(notificationId);
    }
}