package org.harmoniapp.harmoniwebapi.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.Notification;
import org.harmoniapp.harmonidata.entities.Shift;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.configuration.Principle;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

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
}
