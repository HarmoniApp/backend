package org.harmoniapp.contracts.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.harmoniapp.entities.profile.Address;

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
@Builder
public record AddressDto(
        long id,

        @NotEmpty(message = "Zip code cannot be empty")
        @Size(min = 5, max = 10, message = "Zip code must be between 5 and 10 characters")
        @Pattern(regexp = "^[0-9\\-]+$", message = "Zip code must contain only digits and optional dashes")
        @JsonProperty("zip_code") String zipCode,

        @NotEmpty(message = "City cannot be empty")
        @Size(max = 50, message = "City must be less than or equal to 50 characters")
        @Pattern(regexp = "^[A-Za-z Ā-ɏØ-öø-ÿ'\\-\\s]+$", message = "City must contain only letters, spaces and dashes")
        String city,

        @NotEmpty(message = "Street cannot be empty")
        @Size(max = 100, message = "Street must be less than or equal to 100 characters")
        @Pattern(regexp = "^[A-Za-z Ā-ɏØ-öø-ÿ',\\-\\s]+$", message = "Street must contain only letters, spaces, commas and dashes")
        String street,

        @NotEmpty(message = "Building number cannot be empty")
        @Size(min = 1, max = 10, message = "Building number must be between 1 and 10 characters")
        @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Building number must contain only alphanumeric characters")
        @JsonProperty("building_number") String buildingNumber,

        @Size(max = 10, message = "Apartment number must be less than or equal to 10 characters")
        @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Apartment number must contain only alphanumeric characters")
        String apartment,

        @Size(max = 100, message = "Department name must be less than or equal to 100 characters")
        @Pattern(regexp = "^[A-Za-z Ā-ɏØ-öø-ÿ'\\-\\s]+$", message = "Department name must contain only letters, numbers, and spaces")
        @JsonProperty("department_name") String departmentName) {

    /**
     * Converts an Address entity to an AddressDto.
     *
     * @param address The Address entity to be converted.
     * @return An AddressDto representing the Address entity.
     */
    public static AddressDto fromEntity(Address address) {
        return AddressDto.builder()
                .id(address.getId())
                .zipCode(address.getZipCode())
                .city(address.getCity())
                .street(address.getStreet())
                .buildingNumber(address.getBuildingNumber())
                .apartment(address.getApartment())
                .departmentName(address.getDepartmentName())
                .build();
    }

    /**
     * Converts this AddressDto to an Address entity.
     *
     * @return An Address entity representing this AddressDto.
     */
    public Address toEntity() {
        return Address.builder()
                .id(id)
                .zipCode(zipCode)
                .city(city)
                .street(street)
                .buildingNumber(buildingNumber)
                .apartment(apartment)
                .departmentName(departmentName)
                .build();
    }
}
