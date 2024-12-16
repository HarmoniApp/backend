package org.harmoniapp.services.profile;

import org.harmoniapp.contracts.profile.AddressDto;
import org.harmoniapp.contracts.profile.DepartmentDto;
import org.harmoniapp.entities.profile.Address;
import org.harmoniapp.services.CrudService;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * Service interface for managing addresses.
 */
public interface AddressService extends CrudService<AddressDto> {
    /**
     * Retrieves all departments.
     *
     * @return a list of DepartmentDto objects.
     */
    List<DepartmentDto> getAllDepartments();

    /**
     * Saves a new address entity.
     *
     * @param addressDto the address data transfer object.
     * @return the saved Address entity.
     */
    Address saveAddressEntity(AddressDto addressDto);

    /**
     * Updates an existing address entity.
     *
     * @param existingAddress the existing Address entity, can be null.
     * @param addressDto      the address data transfer object.
     * @return the updated Address entity.
     */
    Address update(@Nullable Address existingAddress, AddressDto addressDto);
}
