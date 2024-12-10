package org.harmoniapp.services.auth;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.entities.notification.Notification;
import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.configuration.Principle;
import org.harmoniapp.contracts.absence.AbsenceDto;
import org.harmoniapp.contracts.chat.MessageDto;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing security operations.
 * Provides methods to check if a user is the owner of a notification or shift.
 */
@Service("securityService")
@RequiredArgsConstructor
public class SecurityService {
    private final RepositoryCollector repositoryCollector;

    /**
     * Checks if the authenticated user is the owner of a notification.
     *
     * @param notificationId the ID of the notification to check
     * @param authentication the {@link Authentication} object containing the user's authentication details
     * @return {@code true} if the user is the owner of the notification, {@code false} otherwise
     */
    public boolean isNotificationOwner(Long notificationId, Authentication authentication) {
        Notification notification = repositoryCollector.getNotifications().findById(notificationId).orElse(null);
        if (notification == null) return false;

        Principle principle = (Principle) authentication.getPrincipal();

        return notification.getUser().getId().equals(principle.id());
    }

    /**
     * Checks if the authenticated user is the owner of a shift.
     *
     * @param shiftId the ID of the shift to check
     * @param authentication the {@link Authentication} object containing the user's authentication details
     * @return {@code true} if the user is the owner of the shift, {@code false} otherwise
     */
    public boolean isShiftOwner(Long shiftId, Authentication authentication) {
        Shift shift = repositoryCollector.getShifts().findById(shiftId).orElse(null);
        if (shift == null) return false;

        Principle principle = (Principle) authentication.getPrincipal();

        return shift.getUser().getId().equals(principle.id());
    }

    /**
     * Checks if the authenticated user is the owner of all absences in the provided list.
     *
     * @param absenceDto the list of AbsenceDto objects to check
     * @param authentication the {@link Authentication} object containing the user's authentication details
     * @return {@code true} if the user is the owner of all absences, {@code false} otherwise
     */
    public boolean isAbsenceOwner(List<AbsenceDto> absenceDto, Authentication authentication) {
        Principle principle = (Principle) authentication.getPrincipal();
        for (AbsenceDto absence : absenceDto) {
            if (!absence.userId().equals(principle.id())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the authenticated user is the owner od an absence.
     *
     * @param id id of the absence to check
     * @param authentication the {@link Authentication} object containing the user's authentication details
     * @return {@code true} if the user is the owner of all absences, {@code false} otherwise
     */
    public boolean isAbsenceOwner(long id, Authentication authentication) {
        Principle principle = (Principle) authentication.getPrincipal();
        return repositoryCollector.getAbsences().existsByUserIdAndId(principle.id(), id);
    }

    /**
     * Checks if the authenticated user can send a message.
     *
     * @param messageDto the {@link MessageDto} containing the message details
     * @param authentication the {@link Authentication} object containing the user's authentication details
     * @return {@code true} if the user can send the message, {@code false} otherwise
     */
    public boolean canSendMessage(MessageDto messageDto, Authentication authentication) {
        Principle principle = (Principle) authentication.getPrincipal();
        if (!messageDto.senderId().equals(principle.id())) {
            return false;
        }
        if (messageDto.groupId() != null) {
            return repositoryCollector.getGroups().findById(messageDto.groupId())
                    .map(group -> group.getMembers().stream().anyMatch(user -> user.getId().equals(principle.id())))
                    .orElse(false);
        }
        return true;
    }

    /**
     * Checks if the authenticated user can mark all messages as read.
     *
     * @param userId1 the ID of the user to check
     * @param groupId the ID of the group to check
     * @param authentication the {@link Authentication} object containing the user's authentication details
     * @return {@code true} if the user can mark all messages as read, {@code false} otherwise
     */
    public boolean canMarkAllMessagesAsRead(Long userId1, Long groupId, Authentication authentication) {
        Principle principle = (Principle) authentication.getPrincipal();
        if (groupId != null && !isGroupMember(groupId, principle.id())) {
            return false;
        }
        return userId1.equals(principle.id());
    }

    /**
     * Checks if the user is a member of the specified group.
     *
     * @param groupId the ID of the group to check
     * @param userId the ID of the user to check
     * @return {@code true} if the user is a member of the group, {@code false} otherwise
     */
    private boolean isGroupMember(Long groupId, Long userId) {
        return repositoryCollector.getGroups().findById(groupId)
                .map(group -> group.getMembers().stream().anyMatch(user -> user.getId().equals(userId)))
                .orElse(false);
    }
}
