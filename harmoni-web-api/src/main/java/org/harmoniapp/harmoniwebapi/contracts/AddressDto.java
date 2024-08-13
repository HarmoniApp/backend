package org.harmoniapp.harmoniwebapi.contracts;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.harmoniapp.harmonidata.entities.Address;

/**
 * Data Transfer Object for Address.
 *
 * @param id             The unique identifier for the address.
 * @param zipCode        The postal code of the address.
 * @param city           The city where the address is located.
 * @param street         The street name of the address.
 * @param buildingNumber The building number of the address.
 * @param apartment      The apartment number of the address, if applicable.
 * @param departmentName The department name of the company
 */
public record AddressDto(
        long id,
        @JsonProperty("zip_code") String zipCode,
        String city,
        String street,
        @JsonProperty("building_number") String buildingNumber,
        String apartment,
        @JsonProperty("department_name") String departmentName) {

    /**
     * Converts an Address entity to an AddressDto.
     *
     * @param address The Address entity to be converted.
     * @return An AddressDto representing the Address entity.
     */
    public static AddressDto fromEntity(Address address) {
        return new AddressDto(
                address.getId(),
                address.getZipCode(),
                address.getCity(),
                address.getStreet(),
                address.getBuildingNumber(),
                address.getApartment(),
                address.getDepartmentName()
        );
    }

    /**
     * Converts this AddressDto to an Address entity.
     *
     * @return An Address entity representing this AddressDto.
     */
    public Address toEntity() {
        return new Address(
                id,
                zipCode,
                city,
                street,
                buildingNumber,
                apartment,
                departmentName
        );
    }
}
