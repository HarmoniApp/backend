package org.harmoniapp.services.profile;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.profile.AddressDto;
import org.harmoniapp.contracts.profile.DepartmentDto;
import org.harmoniapp.entities.profile.Address;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.exception.EntityNotFoundException;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for managing addresses.
 */
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final RepositoryCollector repositoryCollector;

    /**
     * Retrieves an AddressDto by its ID.
     *
     * @param id the ID of the address to retrieve
     * @return the AddressDto corresponding to the given ID
     * @throws EntityNotFoundException if no address is found with the given ID
     */
    @Override
    public AddressDto getById(long id) {
        Address address = getAddressById(id);
        return AddressDto.fromEntity(address);
    }

    /**
     * Retrieves an Address entity by its ID.
     *
     * @param id the ID of the address to retrieve
     * @return the Address entity corresponding to the given ID
     * @throws EntityNotFoundException if no address is found with the given ID
     */
    private Address getAddressById(long id) {
        return repositoryCollector.getAddresses().findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono adresu o id: " + id));
    }

    /**
     * Retrieves all AddressDto objects.
     *
     * @return a list of all AddressDto objects
     */
    @Override
    public List<AddressDto> getAll() {
        return repositoryCollector.getAddresses()
                .findAll()
                .stream()
                .map(AddressDto::fromEntity)
                .toList();
    }

    /**
     * Retrieves all DepartmentDto objects.
     *
     * @return a list of all DepartmentDto objects
     */
    @Override
    public List<DepartmentDto> getAllDepartments() { //TODO: add caching
        return repositoryCollector.getAddresses()
                .findByDepartmentNameNotNull()
                .stream()
                .map(DepartmentDto::fromEntity)
                .toList();
    }
    //TODO: extract
    public List<AddressDto> getAllDepartmentsAddress() {
        return repositoryCollector.getAddresses()
                .findByDepartmentNameNotNull()
                .stream()
                .map(AddressDto::fromEntity)
                .toList();
    }

    /**
     * Creates a new Address entity from the given AddressDto.
     *
     * @param addressDto the AddressDto containing the details of the address to create
     * @return the created AddressDto
     */
    @Override
    @Transactional
    public AddressDto create(AddressDto addressDto) {
        Address address = saveAddressEntity(addressDto);
        return AddressDto.fromEntity(address);
    }

    /**
     * Saves an Address entity from the given AddressDto.
     *
     * @param addressDto the AddressDto containing the details of the address to save
     * @return the saved Address entity
     */
    @Override
    @Transactional
    public Address saveAddressEntity(AddressDto addressDto) {
        Address address = addressDto.toEntity();
        return repositoryCollector.getAddresses().save(address);
    }

    /**
     * Updates an Address entity by its ID with the details from the given AddressDto.
     *
     * @param id         the ID of the address to update
     * @param addressDto the AddressDto containing the updated details
     * @return the updated AddressDto
     */
    @Override
    @Transactional
    public AddressDto updateById(long id, AddressDto addressDto) {
        Address existingAddress = null;
        try {
            existingAddress = getAddressById(id);
        } catch (EntityNotFoundException ignored) {
        }
        Address updatedAddress = update(existingAddress, addressDto);
        return AddressDto.fromEntity(repositoryCollector.getAddresses().save(updatedAddress));
    }

    /**
     * Updates an Address entity with the details from the given AddressDto.
     *
     * @param existingAddress the existing Address entity to update, can be null
     * @param addressDto      the AddressDto containing the updated details
     * @return the updated Address entity
     */
    @Override
    @Transactional
    public Address update(@Nullable Address existingAddress, AddressDto addressDto) {
        Address newAddress = addressDto.toEntity();
        newAddress.setId((existingAddress != null) ? existingAddress.getId() : null);

        return repositoryCollector.getAddresses().save(newAddress);
    }

    /**
     * Deletes an Address entity by its ID.
     *
     * @param id the ID of the address to delete
     * @throws EntityNotFoundException if no address is found with the given ID
     */
    @Override
    @Transactional
    public void deleteById(long id) {
        addressExists(id);
        removeAddressFromUsers(id);
        repositoryCollector.getAddresses().deleteById(id);
    }

    /**
     * Checks if an address exists by its ID.
     *
     * @param id the ID of the address to check
     * @throws EntityNotFoundException if no address is found with the given ID
     */
    private void addressExists(long id) {
        if (!repositoryCollector.getAddresses().existsById(id)) {
            throw new EntityNotFoundException("Nie znaleziono adresu o id: " + id);
        }
    }

    /**
     * Removes the address with the given ID from all users' residence and work addresses.
     *
     * @param id the ID of the address to remove from users
     */
    private void removeAddressFromUsers(long id) {
        List<User> users = repositoryCollector.getUsers().findByResidence_IdOrWorkAddress_Id(id);
        if (!users.isEmpty()) {
            users.forEach(user -> {
                if (user.getResidence().getId().equals(id)) {
                    user.setResidence(null);
                }
                if (user.getWorkAddress().getId().equals(id)) {
                    user.setWorkAddress(null);
                }
            });
            repositoryCollector.getUsers().saveAll(users);
        }
    }
}
