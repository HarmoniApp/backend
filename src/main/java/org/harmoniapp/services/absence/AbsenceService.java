package org.harmoniapp.services.absence;

import org.harmoniapp.contracts.PageDto;
import org.harmoniapp.contracts.absence.AbsenceDto;

/**
 * Service interface for managing absences.
 */
public interface AbsenceService {

    /**
     * Retrieves a paginated list of absences for a specific user.
     *
     * @param id         the ID of the user
     * @param pageNumber the page number to retrieve
     * @param pageSize   the number of items per page
     * @return a paginated list of absences
     */
    PageDto<AbsenceDto> getByUserId(long id, int pageNumber, int pageSize);

    /**
     * Retrieves a paginated list of absences by status.
     *
     * @param statusId   the ID of the status
     * @param pageNumber the page number to retrieve
     * @param pageSize   the number of items per page
     * @return a paginated list of absences
     */
    PageDto<AbsenceDto> getByStatus(long statusId, int pageNumber, int pageSize);

    /**
     * Retrieves a paginated list of all absences.
     *
     * @param pageNumber the page number to retrieve
     * @param pageSize   the number of items per page
     * @return a paginated list of all absences
     */
    PageDto<AbsenceDto> getAll(int pageNumber, int pageSize);

    /**
     * Creates a new absence.
     *
     * @param absenceDto the absence data transfer object
     * @return the created absence
     */
    AbsenceDto create(AbsenceDto absenceDto);

    /**
     * Updates the status of an existing absence.
     *
     * @param id       the ID of the absence to update
     * @param statusId the new status ID
     * @return the updated absence
     */
    AbsenceDto updateStatus(long id, long statusId);

    /**
     * Deletes an absence by ID and status ID.
     *
     * @param id       the ID of the absence to delete
     * @param statusId the status ID of the absence to delete
     */
    void deleteAbsence(long id, long statusId);
}
