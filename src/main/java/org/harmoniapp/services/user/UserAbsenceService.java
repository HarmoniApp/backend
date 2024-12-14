package org.harmoniapp.services.user;

import org.harmoniapp.entities.user.User;

/**
 * Service interface for managing user absences.
 */
public interface UserAbsenceService extends FindUser {
    /**
     * Retrieves the number of available absence days for a user.
     *
     * @param id the ID of the user
     * @return the number of available absence days
     */
    int getUserAvailableAbsenceDays(long id);

    /**
     * Carries over the previous year's unused absence days for a user.
     *
     * @param user the user whose absence days are to be carried over
     */
    void carryOverPreviousYearAbsenceDays(User user);

    /**
     * Expires the unused absence days for a user.
     *
     * @param user the user whose unused absence days are to be expired
     */
    void expireUnusedAbsenceDays(User user);
}
