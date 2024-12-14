package org.harmoniapp.services.user;

import org.harmoniapp.contracts.PageDto;
import org.harmoniapp.contracts.PageRequestDto;
import org.harmoniapp.contracts.user.SupervisorDto;

/**
 * Service interface for managing supervisors.
 */
public interface SupervisorService {

    /**
     * Retrieves a paginated list of supervisors.
     *
     * @param pageRequest the pagination and sorting information
     * @return a paginated list of SupervisorDto objects
     */
    PageDto<SupervisorDto> getAllSupervisors(PageRequestDto pageRequest);
}
