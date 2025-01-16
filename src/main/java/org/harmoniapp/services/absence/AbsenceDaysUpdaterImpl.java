package org.harmoniapp.services.absence;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.entities.absence.Absence;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.exception.AbsenceDaysExceededException;
import org.harmoniapp.exception.EntityNotFoundException;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for updating user's absence days.
 */
@Service
@RequiredArgsConstructor
public class AbsenceDaysUpdaterImpl implements AbsenceDaysUpdater {
    private final RepositoryCollector repositoryCollector;

    /**
     * Updates the user's available and unused absence days.
     *
     * @param user          the User entity
     * @param requestedDays the number of requested days
     * @throws AbsenceDaysExceededException if the requested days exceed available days
     */
    @Override
    @Transactional
    public void updateUserAbsenceDays(User user, int requestedDays) {
        int availableDays = user.getAvailableAbsenceDays() + user.getUnusedAbsenceDays();

        if (requestedDays > availableDays) {
            throw new AbsenceDaysExceededException("Nie można wziąć więcej dni niż jest dostępnych");
        }

        int unusedDays = user.getUnusedAbsenceDays();
        if (requestedDays <= unusedDays) {
            user.setUnusedAbsenceDays(unusedDays - requestedDays);
        } else {
            user.setUnusedAbsenceDays(0);
            user.setAvailableAbsenceDays(user.getAvailableAbsenceDays() - (requestedDays - unusedDays));
        }
        repositoryCollector.getUsers().save(user);
    }

    /**
     * Updates the user's available and unused absence days based on the given Absence entity.
     *
     * @param absence the Absence entity containing the absence details
     * @throws EntityNotFoundException if the user associated with the absence is not found
     */
    @Override
    @Transactional
    public void updateUserAbsenceDays(Absence absence) {
        User user = repositoryCollector.getUsers().findById(absence.getUser().getId())
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono użytkownika"));

        int contractAbsenceDays = user.getContractType().getAbsenceDays();
        int availableDays = user.getAvailableAbsenceDays();
        int bookedDays = absence.getWorkingDays().intValue();

        if (availableDays < contractAbsenceDays) {
            int rest = contractAbsenceDays - availableDays;
            user.setAvailableAbsenceDays(availableDays + rest);
            bookedDays -= rest;
        }
        user.setUnusedAbsenceDays(bookedDays);
        repositoryCollector.getUsers().save(user);
    }
}
