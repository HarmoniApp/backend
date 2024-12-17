package org.harmoniapp.services.profile;

import org.harmoniapp.contracts.profile.RoleDto;
import org.harmoniapp.services.CrudService;

import java.util.List;

/**
 * Service interface for managing roles.
 */
public interface RoleService extends CrudService<RoleDto> {

    /**
     * Retrieves roles for a specific user by user ID.
     *
     * @param id the ID of the user
     * @return a list of role DTOs for the user
     */
    List<RoleDto> getUserRoles(long id);
}
