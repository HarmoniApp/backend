package org.harmoniapp.contracts.profile;

import org.harmoniapp.entities.profile.Address;

/**
 * A Data Transfer Object for Department.
 *
 * @param id             the unique identifier of the department
 * @param departmentName the name of the department
 */
public record DepartmentDto(long id, String departmentName) {

    /**
     * Converts an Address entity to a DepartmentDto.
     *
     * @param address the Address entity
     * @return a new DepartmentDto with values from the Address entity
     */
    public static DepartmentDto fromEntity(Address address) {
        return new DepartmentDto(address.getId(), address.getDepartmentName());
    }
}
