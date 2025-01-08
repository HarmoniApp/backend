package org.harmoniapp.services.user;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.exception.EntityNotFoundException;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Service implementation for managing user absences.
 */
@Service
@RequiredArgsConstructor
public class UserAbsenceServiceImpl implements UserAbsenceService {
    private final RepositoryCollector repositoryCollector;

    /**
     * Retrieves the total number of available absence days for a user.
     *
     * @param id the ID of the user
     * @return the sum of available absence days and unused absence days
     */
    @Override
    public int getUserAvailableAbsenceDays(long id) {
        User user = repositoryCollector.getUsers().findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkowika"));
        return user.getAvailableAbsenceDays() + user.getUnusedAbsenceDays();
    }

    /**
     * Carries over the available absence days from the previous year for a user.
     *
     * @param user the user whose absence days are to be carried over
     */
    @Override
    public void carryOverPreviousYearAbsenceDays(User user) {
        carryOverAvailableAbsenceDays(user);
        resetAvailableAbsenceDays(user);
        repositoryCollector.getUsers().save(user);
    }

    /**
     * Expires the unused absence days for a user if the expiration date has passed.
     *
     * @param user the user whose unused absence days are to be expired
     */
    @Override
    public void expireUnusedAbsenceDays(User user) {
        if (user.getUnusedAbsenceExpiration() != null && user.getUnusedAbsenceExpiration().isBefore(LocalDate.now())) {
            user.setUnusedAbsenceDays(0);
            user.setUnusedAbsenceExpiration(user.getUnusedAbsenceExpiration().plusYears(1));
            repositoryCollector.getUsers().save(user);
        }
    }

    /**
     * Carries over the available absence days to unused absence days for a user.
     *
     * @param user the user whose available absence days are to be carried over
     */
    private void carryOverAvailableAbsenceDays(User user) {
        if (user.getAvailableAbsenceDays() > 0) {
            user.setUnusedAbsenceDays(user.getAvailableAbsenceDays());
            user.setUnusedAbsenceExpiration(LocalDate.of(LocalDate.now().getYear() + 1, 9, 30));
            user.setAvailableAbsenceDays(0);
        }
    }

    /**
     * Resets the available absence days for a user based on their contract type.
     *
     * @param user the user whose available absence days are to be reset
     * @throws EntityNotFoundException if the contract type is not found
     */
    private void resetAvailableAbsenceDays(User user) throws EntityNotFoundException {
        int newAbsenceDays = repositoryCollector.getContractTypes()
                .findById(user.getContractType().getId())
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono typu umowy"))
                .getAbsenceDays();
        user.setAvailableAbsenceDays(newAbsenceDays);
    }

    /**
     * Scheduled task to carry over the available absence days from the previous year for all active users.
     * This task runs every year on January 1st at 00:00.
     */
    @Scheduled(cron = "0 0 0 1 1 ?")
    public void scheduledCarryOverPreviousYearAbsenceDays() {
        List<User> users = repositoryCollector.getUsers().findAllByIsActiveTrue();
        for (User user : users) {
            carryOverPreviousYearAbsenceDays(user);
        }
    }

    /**
     * Scheduled task to expire the unused absence days for all active users.
     * This task runs every year on October 1st at 00:00.
     */
    @Scheduled(cron = "0 0 0 1 10 ?")
    public void scheduledExpireUnusedAbsenceDays() {
        List<User> users = repositoryCollector.getUsers().findAllByIsActiveTrue();
        for (User user : users) {
            expireUnusedAbsenceDays(user);
        }
    }
}
