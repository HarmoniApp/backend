package org.harmoniapp.services.profile;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.entities.profile.Address;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.contracts.profile.AddressDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for managing addresses.
 * Provides methods to perform CRUD operations on addresses.
 */
@Service
@RequiredArgsConstructor
public class AddressService {
    private final RepositoryCollector repositoryCollector;

    /**
     * Retrieves all addresses.
     *
     * @return A list of all AddressDto objects.
     */
    public List<AddressDto> getAllAddresses() {
        List<Address> addresses = repositoryCollector.getAddresses().findAll();

        return addresses.stream().map(AddressDto::fromEntity).toList();
    }

    /**
     * Retrieves all departments.
     *
     * @return A list of all departments.
     */
    @Transactional
    public List<Map<String, Object>> getAllDepartments() {
        List<Address> addresses = repositoryCollector.getAddresses().findAll();

        return addresses.stream()
                .filter(address -> address.getDepartmentName() != null)
                .map(address -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", address.getId());
                    map.put("departmentName", address.getDepartmentName());
                    return map;
                }).toList();
    }


    /**
     * Retrieves an address by its ID.
     *
     * @param id The ID of the address to retrieve.
     * @return The AddressDto object corresponding to the specified ID.
     * @throws EntityNotFoundException if the address with the specified ID is not found.
     */
    public AddressDto getAddressById(Long id) {
        Address address = repositoryCollector.getAddresses().findById(id).orElseThrow(EntityNotFoundException::new);

        return AddressDto.fromEntity(address);
    }

    /**
     * Creates a new address.
     *
     * @param addressDto The AddressDto object representing the new address.
     * @return The created AddressDto object.
     */
    public AddressDto createAddress(AddressDto addressDto) {
        Address address = saveAddressEntity(addressDto);

        return AddressDto.fromEntity(address);
    }

    /**
     * Saves an Address entity.
     *
     * @param addressDto The AddressDto object to be saved.
     * @return The saved Address entity.
     */
    public Address saveAddressEntity(AddressDto addressDto) {
        Address address = addressDto.toEntity();

        return repositoryCollector.getAddresses().save(address);
    }

    /**
     * Updates an existing address.
     *
     * @param id         The ID of the address to update.
     * @param addressDto The AddressDto object containing the updated address data.
     * @return The updated AddressDto object.
     */
    public AddressDto updateAddress(long id, AddressDto addressDto) {
        var existingAddress = repositoryCollector.getAddresses().findById(id);

        Address newAddress = addressDto.toEntity();
        newAddress.setId(existingAddress.map(Address::getId).orElse(null));

        return AddressDto.fromEntity(repositoryCollector.getAddresses().save(newAddress));
    }

    /**
     * Updates an existing address entity.
     *
     * @param existingAddress The existing Address entity.
     * @param addressDto      The AddressDto object containing the updated address data.
     * @return The updated Address entity.
     */
    public Address updateAddress(Address existingAddress, AddressDto addressDto) {
        Address newAddress = addressDto.toEntity();
        newAddress.setId(existingAddress.getId());

        return repositoryCollector.getAddresses().save(newAddress);
    }

    /**
     * Deletes an address by its ID.
     * If the address is associated with any users, it removes the address from those users before deleting it.
     *
     * @param id The ID of the address to delete.
     * @throws EntityNotFoundException if the address with the specified ID is not found.
     */
    public void deleteAddress(long id) {
        if(!repositoryCollector.getAddresses().existsById(id)) {
            throw new EntityNotFoundException("Address with ID " + id + " not found");
        }

        List<User> userList = repositoryCollector.getUsers().findByResidence_IdOrWorkAddress_Id(id);
        for (User user : userList) {
            if (user.getResidence().getId().equals(id)) {
                user.setResidence(null);
            }
            if (user.getWorkAddress().getId().equals(id)) {
                user.setWorkAddress(null);
            }
        }
        repositoryCollector.getUsers().saveAll(userList);

        repositoryCollector.getAddresses().deleteById(id);
    }
}
