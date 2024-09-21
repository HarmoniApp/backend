package org.harmoniapp.harmoniwebapi.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.Notification;
import org.harmoniapp.harmonidata.entities.Shift;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.configuration.Principle;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service("securityService")
@RequiredArgsConstructor
public class SecurityService {
    private final RepositoryCollector repositoryCollector;

    public boolean isNotificationOwner(Long notificationId, Authentication authentication) {
        Notification notification = repositoryCollector.getNotifications().findById(notificationId).orElse(null);
        if (notification == null) return false;

        Principle principle = (Principle) authentication.getPrincipal();

        return notification.getUser().getId().equals(principle.id());
    }

    public boolean isShiftOwner(Long shiftId, Authentication authentication) {
        Shift shift = repositoryCollector.getShifts().findById(shiftId).orElse(null);
        if (shift == null) return false;

        Principle principle = (Principle) authentication.getPrincipal();

        return shift.getUser().getId().equals(principle.id());
    }
}
