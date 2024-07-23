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
        Address address = saveAddressEntity(addressDto);

        return AddressDto.fromEntity(address);
    }

    public Address saveAddressEntity(AddressDto addressDto) {
        Address address = addressDto.toEntity();

        return repositoryCollector.getAddresses().save(address);
    }

    public AddressDto updateAddress(long id, AddressDto addressDto) {
        var existingAddress = repositoryCollector.getAddresses().findById(id);

        Address newAddress = addressDto.toEntity();
        newAddress.setId(existingAddress.map(Address::getId).orElse(null));

        return AddressDto.fromEntity(repositoryCollector.getAddresses().save(newAddress));
    }

    public Address updateAddress(Address existingAddress, AddressDto addressDto) {
        Address newAddress = addressDto.toEntity();
        newAddress.setId(existingAddress.getId());

        return repositoryCollector.getAddresses().save(newAddress);
    }

    public void deleteAddress(long id) {
        Address address = repositoryCollector.getAddresses()
                .findById(id)
                .orElseThrow(EntityNotFoundException::new);

        repositoryCollector.getAddresses().delete(address);
    }
}
