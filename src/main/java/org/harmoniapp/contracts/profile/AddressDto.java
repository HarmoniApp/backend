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

        @NotEmpty(message = "Kod pocztowy nie może być pusty")
        @Size(min = 5, max = 10, message = "Kod pocztowy musi zawierać od 5 do 10 znaków")
        @Pattern(regexp = "^[0-9\\-]+$", message = "Kod pocztowy musi zawierać tylko cyfry i myślniki")
        @JsonProperty("zip_code") String zipCode,

        @NotEmpty(message = "Miasto nie może być puste")
        @Size(max = 50, message = "Miasto musi zawierać mniej niż 50 znaków")
        @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ'\\-\\s]+$", message = "Miasto musi zawierać tylko litery, spacje, apostrofy i myślniki")
        String city,

        @NotEmpty(message = "Ulica nie może być pusta")
        @Size(max = 100, message = "Ulica musi zawierać mniej niż 100 znaków")
        @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ',\\-\\s]+$", message = "Ulica musi zawierać tylko litery, spacje, apostrofy i myślniki")
        String street,

        @NotEmpty(message = "Numer budynku nie może być pusty")
        @Size(min = 1, max = 10, message = "Numer budynku musi zawierać od 1 do 10 znaków")
        @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Numer budynku musi zawierać tylko litery i cyfry")
        @JsonProperty("building_number") String buildingNumber,

        @Size(max = 10, message = "Numer mieszkania musi zawierać mniej niż 10 znaków")
        @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Numer mieszkania musi zawierać tylko litery i cyfry")
        String apartment,

        @Size(max = 100, message = "Nazwa oddziału musi zawierać mniej niż 100 znaków")
        @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ'\\-\\s]+$", message = "Nazwa oddziału musi zawierać tylko litery, spacje, apostrofy i myślniki")
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
