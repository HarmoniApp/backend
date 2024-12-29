package org.harmoniapp.services.auth;

import org.harmoniapp.contracts.absence.AbsenceDto;
import org.harmoniapp.contracts.chat.ChatRequestDto;
import org.harmoniapp.contracts.chat.MessageDto;
import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * Service interface for security-related operations.
 */
public interface SecurityService {

    /**
     * Checks if the authenticated user is the owner of the specified notification.
     *
     * @param notificationId the ID of the notification
     * @param authentication the authentication object of the current user
     * @return true if the user is the owner, false otherwise
     */
    boolean isNotificationOwner(Long notificationId, Authentication authentication);

    /**
     * Checks if the authenticated user is the owner of the specified shift.
     *
     * @param shiftId        the ID of the shift
     * @param authentication the authentication object of the current user
     * @return true if the user is the owner, false otherwise
     */
    boolean isShiftOwner(Long shiftId, Authentication authentication);

    /**
     * Checks if the authenticated user is the owner of the specified absences.
     *
     * @param absenceDto     the list of absence DTOs
     * @param authentication the authentication object of the current user
     * @return true if the user is the owner, false otherwise
     */
    boolean isAbsenceOwner(List<AbsenceDto> absenceDto, Authentication authentication);

    /**
     * Checks if the authenticated user is the owner of the specified absence.
     *
     * @param id             the ID of the absence
     * @param authentication the authentication object of the current user
     * @return true if the user is the owner, false otherwise
     */
    boolean isAbsenceOwner(long id, Authentication authentication);

    /**
     * Checks if the authenticated user can send the specified message.
     *
     * @param messageDto     the message DTO
     * @param authentication the authentication object of the current user
     * @return true if the user can send the message, false otherwise
     */
    boolean canSendMessage(MessageDto messageDto, Authentication authentication);

    /**
     * Checks if the authenticated user can mark all messages as read in the specified chat.
     *
     * @param chatRequestDto the chat request DTO
     * @param authentication the authentication object of the current user
     * @return true if the user can mark all messages as read, false otherwise
     */
    boolean canMarkAllMessagesAsRead(ChatRequestDto chatRequestDto, Authentication authentication);
}
