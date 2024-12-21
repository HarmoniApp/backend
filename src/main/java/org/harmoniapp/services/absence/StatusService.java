package org.harmoniapp.services.absence;

import org.harmoniapp.contracts.absence.StatusDto;

import java.util.List;

/**
 * Service interface for managing status operations.
 */
public interface StatusService {

    /**
     * Retrieves all statuses.
     *
     * @return a list of StatusDto objects representing all statuses.
     */
    List<StatusDto> getAllStatuses();
}
