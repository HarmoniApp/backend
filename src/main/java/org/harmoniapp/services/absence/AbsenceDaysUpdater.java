package org.harmoniapp.services.absence;

import org.harmoniapp.entities.absence.Absence;
import org.harmoniapp.entities.user.User;

/**
 * Interface for updating user absence days.
 */
public interface AbsenceDaysUpdater {

    /**
     * Updates the absence days for a user based on the requested days.
     *
     * @param user          the user whose absence days are to be updated
     * @param requestedDays the number of requested absence days
     */
    void updateUserAbsenceDays(User user, int requestedDays);

    /**
     * Updates the absence days for a user based on an Absence object.
     *
     * @param absence the Absence object containing the absence details
     */
    void updateUserAbsenceDays(Absence absence);

}
