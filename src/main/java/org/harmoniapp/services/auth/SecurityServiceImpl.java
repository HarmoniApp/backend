package org.harmoniapp.services.auth;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.configuration.Principle;
import org.harmoniapp.contracts.absence.AbsenceDto;
import org.harmoniapp.contracts.chat.ChatRequestDto;
import org.harmoniapp.contracts.chat.MessageDto;
import org.harmoniapp.entities.notification.Notification;
import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("securityService")
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {
    private final RepositoryCollector repositoryCollector;

    /**
     * Checks if the authenticated user is the owner of the specified notification.
     *
     * @param notificationId the ID of the notification
     * @param authentication the authentication object containing the user's details
     * @return true if the authenticated user is the owner of the notification, false otherwise
     */
    @Override
    public boolean isNotificationOwner(Long notificationId, Authentication authentication) {
        Notification notification = repositoryCollector.getNotifications().findById(notificationId).orElse(null);
        if (notification == null) return false;

        Principle principle = (Principle) authentication.getPrincipal();
        return notification.getUser().getId().equals(principle.id());
    }

    /**
     * Checks if the authenticated user is the owner of the specified shift.
     *
     * @param shiftId        the ID of the shift
     * @param authentication the authentication object containing the user's details
     * @return true if the authenticated user is the owner of the shift, false otherwise
     */
    @Override
    public boolean isShiftOwner(Long shiftId, Authentication authentication) {
        Shift shift = repositoryCollector.getShifts().findById(shiftId).orElse(null);
        if (shift == null) return false;

        Principle principle = (Principle) authentication.getPrincipal();
        return shift.getUser().getId().equals(principle.id());
    }

    /**
     * Checks if the authenticated user is the owner of all the specified absences.
     *
     * @param absenceDto     the list of absence DTOs
     * @param authentication the authentication object containing the user's details
     * @return true if the authenticated user is the owner of all the absences, false otherwise
     */
    @Override
    public boolean isAbsenceOwner(List<AbsenceDto> absenceDto, Authentication authentication) {
        Long userId = ((Principle) authentication.getPrincipal()).id();
        return absenceDto.stream().allMatch(absence -> absence.userId().equals(userId));
    }

    /**
     * Checks if the authenticated user is the owner of the specified absence.
     *
     * @param id             the ID of the absence
     * @param authentication the authentication object containing the user's details
     * @return true if the authenticated user is the owner of the absence, false otherwise
     */
    @Override
    public boolean isAbsenceOwner(long id, Authentication authentication) {
        Principle principle = (Principle) authentication.getPrincipal();
        return repositoryCollector.getAbsences().existsByUserIdAndId(principle.id(), id);
    }

    /**
     * Checks if the authenticated user can send the specified message.
     *
     * @param messageDto     the message DTO containing the message details
     * @param authentication the authentication object containing the user's details
     * @return true if the authenticated user can send the message, false otherwise
     */
    @Override
    public boolean canSendMessage(MessageDto messageDto, Authentication authentication) {
        Principle principle = (Principle) authentication.getPrincipal();
        if (!messageDto.senderId().equals(principle.id())) {
            return false;
        }
        return messageDto.groupId() == null || isGroupMember(messageDto.groupId(), principle.id());
    }

    /**
     * Checks if the authenticated user can mark all messages as read.
     *
     * @param chatRequestDto the chat request DTO containing the chat details
     * @param authentication the authentication object containing the user's details
     * @return true if the authenticated user can mark all messages as read, false otherwise
     */
    @Override
    public boolean canMarkAllMessagesAsRead(ChatRequestDto chatRequestDto, Authentication authentication) {
        Principle principle = (Principle) authentication.getPrincipal();
        if (chatRequestDto.groupId() != null && !isGroupMember(chatRequestDto.groupId(), principle.id())) {
            return false;
        }
        if (chatRequestDto.userId1() != null) {
            return chatRequestDto.userId1().equals(principle.id());
        }
        return false;
    }

    /**
     * Checks if the user is a member of the specified group.
     *
     * @param groupId the ID of the group
     * @param userId  the ID of the user
     * @return true if the user is a member of the group, false otherwise
     */
    private boolean isGroupMember(Long groupId, Long userId) {
        return repositoryCollector.getGroups().findById(groupId)
                .map(group -> group.getMembers().stream().anyMatch(user -> user.getId().equals(userId)))
                .orElse(false);
    }
}
