package org.harmoniapp.harmoniwebapi.mappers;

import org.harmoniapp.harmonidata.entities.Address;
import org.harmoniapp.harmoniwebapi.contracts.AddressDto;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper implements MapEntityDto<Address, AddressDto> {
    @Override
    public AddressDto toDto(Address address) {
        return new AddressDto(
                address.getId(),
                address.getZipCode(),
                address.getCity(),
                address.getStreet(),
                address.getBuildingNumber(),
                address.getApartment()
        );
    }

    @Override
    public Address toEntity(AddressDto addressDto) {
        return new Address(
                addressDto.id(),
                addressDto.zipCode(),
                addressDto.city(),
                addressDto.street(),
                addressDto.buildingNumber(),
                addressDto.apartment()
        );
    }
}
