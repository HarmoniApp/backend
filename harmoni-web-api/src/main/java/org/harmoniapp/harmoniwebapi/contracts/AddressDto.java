package org.harmoniapp.harmoniwebapi.contracts;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.harmoniapp.harmonidata.entities.Address;

public record AddressDto(
        long id,
        @JsonProperty("zip_code") String zipCode,
        String city,
        String street,
        @JsonProperty("building_number") String buildingNumber,
        String apartment) {

    public static AddressDto fromEntity(Address address) {
        return new AddressDto(
                address.getId(),
                address.getZipCode(),
                address.getCity(),
                address.getStreet(),
                address.getBuildingNumber(),
                address.getApartment()
        );
    }

    public Address toEntity() {
        return new Address(
                id,
                zipCode,
                city,
                street,
                buildingNumber,
                apartment
        );
    }
}
