package org.harmoniapp.services.absence;

import org.harmoniapp.entities.absence.Absence;

/**
 * Interface for updating shifts related to absences.
 */
public interface ShiftUpdater {

    /**
     * Removes the overlapped shifts for a given absence.
     *
     * @param absence the Absence object containing the absence details
     */
    void removeOverlappedShifts(Absence absence);
}
