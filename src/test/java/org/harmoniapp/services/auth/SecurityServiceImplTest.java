package org.harmoniapp.services.auth;

import org.harmoniapp.configuration.Principle;
import org.harmoniapp.contracts.absence.AbsenceDto;
import org.harmoniapp.contracts.chat.ChatRequestDto;
import org.harmoniapp.contracts.chat.MessageDto;
import org.harmoniapp.entities.chat.Group;
import org.harmoniapp.entities.notification.Notification;
import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.repositories.absence.AbsenceRepository;
import org.harmoniapp.repositories.chat.GroupRepository;
import org.harmoniapp.repositories.notification.NotificationRepository;
import org.harmoniapp.repositories.schedule.ShiftRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SecurityServiceImplTest {

    @Mock
    private RepositoryCollector repositoryCollector;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private AbsenceRepository absenceRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private Principle principle;

    @InjectMocks
    private SecurityServiceImpl securityService;

    @Test
    public void isNotificationOwnerTest() {
        Notification notification = mock(Notification.class);
        User user = mock(User.class);
        when(repositoryCollector.getNotifications()).thenReturn(notificationRepository);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(authentication.getPrincipal()).thenReturn(principle);
        when(principle.id()).thenReturn(1L);
        when(notification.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(1L);

        boolean result = securityService.isNotificationOwner(1L, authentication);

        assertTrue(result);
    }

    @Test
    public void isNotificationOwnerNotFoundTest() {
        when(repositoryCollector.getNotifications()).thenReturn(notificationRepository);
        when(notificationRepository.findById(1L)).thenReturn(Optional.empty());

        boolean result = securityService.isNotificationOwner(1L, authentication);

        assertFalse(result);
    }

    @Test
    public void isShiftOwnerTest() {
        Shift shift = mock(Shift.class);
        User user = mock(User.class);
        when(repositoryCollector.getShifts()).thenReturn(shiftRepository);
        when(shiftRepository.findById(1L)).thenReturn(Optional.of(shift));
        when(authentication.getPrincipal()).thenReturn(principle);
        when(principle.id()).thenReturn(1L);
        when(shift.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(1L);

        boolean result = securityService.isShiftOwner(1L, authentication);

        assertTrue(result);
    }

    @Test
    public void isShiftOwnerNotFoundTest() {
        when(repositoryCollector.getShifts()).thenReturn(shiftRepository);
        when(shiftRepository.findById(1L)).thenReturn(Optional.empty());

        boolean result = securityService.isShiftOwner(1L, authentication);

        assertFalse(result);
    }

    @Test
    public void isAbsenceOwnerTest() {
        AbsenceDto absenceDto = mock(AbsenceDto.class);
        when(authentication.getPrincipal()).thenReturn(principle);
        when(principle.id()).thenReturn(1L);
        when(absenceDto.userId()).thenReturn(1L);

        boolean result = securityService.isAbsenceOwner(List.of(absenceDto), authentication);

        assertTrue(result);
    }

    @Test
    public void isAbsenceOwnerByIdTest() {
        when(authentication.getPrincipal()).thenReturn(principle);
        when(principle.id()).thenReturn(1L);
        when(repositoryCollector.getAbsences()).thenReturn(absenceRepository);
        when(absenceRepository.existsByUserIdAndId(1L, 1L)).thenReturn(true);

        boolean result = securityService.isAbsenceOwner(1L, authentication);

        assertTrue(result);
    }

    @Test
    public void canSendMessageTest() {
        MessageDto messageDto = mock(MessageDto.class);
        when(authentication.getPrincipal()).thenReturn(principle);
        when(principle.id()).thenReturn(1L);
        when(messageDto.senderId()).thenReturn(1L);
        when(messageDto.groupId()).thenReturn(null);

        boolean result = securityService.canSendMessage(messageDto, authentication);

        assertTrue(result);
    }

    @Test
    public void canSendMessageWrongSenderIdTest() {
        MessageDto messageDto = mock(MessageDto.class);
        when(authentication.getPrincipal()).thenReturn(principle);
        when(principle.id()).thenReturn(1L);
        when(messageDto.senderId()).thenReturn(2L);

        boolean result = securityService.canSendMessage(messageDto, authentication);

        assertFalse(result);
    }

    @Test
    public void canMarkAllMessagesAsReadTest() {
        ChatRequestDto chatRequestDto = mock(ChatRequestDto.class);
        when(authentication.getPrincipal()).thenReturn(principle);
        when(principle.id()).thenReturn(1L);
        when(chatRequestDto.groupId()).thenReturn(null);
        when(chatRequestDto.userId1()).thenReturn(1L);

        boolean result = securityService.canMarkAllMessagesAsRead(chatRequestDto, authentication);

        assertTrue(result);
    }

    @Test
    public void canMarkAllMessagesAsReadGroupIdNotMemberTest() {
        ChatRequestDto chatRequestDto = mock(ChatRequestDto.class);
        when(authentication.getPrincipal()).thenReturn(principle);
        when(principle.id()).thenReturn(1L);
        when(chatRequestDto.groupId()).thenReturn(1L);
        when(repositoryCollector.getGroups()).thenReturn(groupRepository);
        Group group = mock(Group.class);
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        User user = mock(User.class);
        when(group.getMembers()).thenReturn(new HashSet<>(List.of(user)));
        when(user.getId()).thenReturn(2L);

        boolean result = securityService.canMarkAllMessagesAsRead(chatRequestDto, authentication);

        assertFalse(result);
    }

    @Test
    public void canMarkAllMessagesAsReadNullValuesTest() {
        ChatRequestDto chatRequestDto = mock(ChatRequestDto.class);
        when(authentication.getPrincipal()).thenReturn(principle);
        when(chatRequestDto.groupId()).thenReturn(null);
        when(chatRequestDto.userId1()).thenReturn(null);

        boolean result = securityService.canMarkAllMessagesAsRead(chatRequestDto, authentication);

        assertFalse(result);
    }
}