package org.harmoniapp.harmoniwebapi.contracts;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AddressDto(
        long id,
        @JsonProperty("zip_code") String zipCode,
        String city,
        String street,
        @JsonProperty("building_number") String buildingNumber,
        String apartment) {
}
