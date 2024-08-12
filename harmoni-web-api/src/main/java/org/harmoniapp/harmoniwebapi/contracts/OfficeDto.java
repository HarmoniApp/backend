package org.harmoniapp.harmoniwebapi.contracts;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.harmoniapp.harmonidata.entities.Address;

//TODO: Update after DB modify
public record OfficeDto(long id,
                        String name,
                        @JsonProperty("zip-code") String zipCode,
                        String city,
                        @JsonProperty("building-number") String buildingNumber,
                        String apartment) {

    public static OfficeDto fromEntity(Address address) {
        return new OfficeDto(
                address.getId(),
                "name" + address.getId(),
                address.getZipCode(),
                address.getCity(),
                address.getBuildingNumber(),
                address.getApartment()
        );
    }
}
