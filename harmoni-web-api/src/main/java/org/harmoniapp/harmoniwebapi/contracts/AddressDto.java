package org.harmoniapp.harmoniwebapi.contracts;

public record AddressDto(String zipcode, String city, String street, String buildingNumber,
                         String apartment) {
}
