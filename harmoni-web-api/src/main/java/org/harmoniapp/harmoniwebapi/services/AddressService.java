package org.harmoniapp.harmoniwebapi.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.Address;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.AddressDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final RepositoryCollector repositoryCollector;

    public List<AddressDto> getAllAddresses() {
        List<Address> addresses = repositoryCollector.getAddresses().findAll();

        return addresses.stream().map(AddressDto::fromEntity).toList();
    }

    public AddressDto getAddressById(Long id) {
        Address address = repositoryCollector.getAddresses().findById(id).orElseThrow(EntityNotFoundException::new);

        return AddressDto.fromEntity(address);
    }

    public AddressDto createAddress(AddressDto addressDto) {
        Address address = addressDto.toEntity();

        return AddressDto.fromEntity(repositoryCollector.getAddresses().save(address));
    }

    public AddressDto updateAddress(long id, AddressDto addressDto) {
        Address address = repositoryCollector.getAddresses().findById(id).orElse(null);

        if (address != null) {
            address.setApartment(addressDto.apartment());
            address.setCity(addressDto.city());
            address.setStreet(addressDto.street());
            address.setZipCode(addressDto.zipCode());
            address.setBuildingNumber(addressDto.buildingNumber());
        } else {
            address = addressDto.toEntity();
        }

        return AddressDto.fromEntity(repositoryCollector.getAddresses().save(address));
    }

    public void deleteAddress(long id) {
        Address address = repositoryCollector.getAddresses()
                .findById(id)
                .orElseThrow(EntityNotFoundException::new);

        repositoryCollector.getAddresses().delete(address);
    }
}
