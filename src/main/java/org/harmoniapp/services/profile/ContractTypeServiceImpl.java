package org.harmoniapp.services.profile;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.profile.ContractTypeDto;
import org.harmoniapp.entities.profile.ContractType;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.exception.EntityNotFoundException;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

/**
 * Service implementation for managing contract types.
 */
@Service
@RequiredArgsConstructor
public class ContractTypeServiceImpl implements ContractTypeService {
    private final RepositoryCollector repositoryCollector;
    private static final String CACHE_NAME = "contractTypes";

    /**
     * Retrieves a ContractTypeDto by its ID.
     *
     * @param id the ID of the contract type
     * @return the ContractTypeDto corresponding to the given ID
     * @throws EntityNotFoundException if no contract type is found with the given ID
     */
    @Override
    public ContractTypeDto getById(long id) {
        ContractType contractType = getContractTypeById(id);
        return ContractTypeDto.fromEntity(contractType);
    }

    /**
     * Retrieves a ContractType entity by its ID.
     *
     * @param id the ID of the contract type
     * @return the ContractType entity corresponding to the given ID
     * @throws EntityNotFoundException if no contract type is found with the given ID
     */
    private ContractType getContractTypeById(long id) {
        return repositoryCollector.getContractTypes().findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono typu umowy o id: " + id));
    }

    /**
     * Retrieves all contract types.
     *
     * @return a list of ContractTypeDto objects representing all contract types
     */
    @Override
    public List<ContractTypeDto> getAll() {
        return repositoryCollector.getContractTypes()
                .findAll()
                .stream()
                .map(ContractTypeDto::fromEntity)
                .sorted(Comparator.comparing(ContractTypeDto::name))
                .toList();
    }

    /**
     * Creates a new ContractType.
     *
     * @param contractTypeDto the DTO representing the contract type to be created
     * @return the created ContractTypeDto
     */
    @Override
    @Transactional
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public ContractTypeDto create(ContractTypeDto contractTypeDto) {
        ContractType contractType = contractTypeDto.toEntity();
        return saveContractType(contractType);
    }

    /**
     * Saves the given ContractType entity and returns its corresponding DTO.
     *
     * @param contractType the ContractType entity to be saved
     * @return the ContractTypeDto corresponding to the saved entity
     */
    private ContractTypeDto saveContractType(ContractType contractType) {
        ContractType savedContractType = repositoryCollector.getContractTypes().save(contractType);
        return ContractTypeDto.fromEntity(savedContractType);
    }

    /**
     * Updates an existing ContractType with the given ID using the provided ContractTypeDto.
     * If the ContractType with the given ID does not exist, a new ContractType is created.
     *
     * @param id              the ID of the contract type to be updated
     * @param contractTypeDto the DTO representing the contract type to be updated
     * @return the updated or created ContractTypeDto
     */
    @Override
    @Transactional
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public ContractTypeDto updateById(long id, ContractTypeDto contractTypeDto) {
        return repositoryCollector.getContractTypes().findById(id)
                .map(ct -> updateContractType(ct, contractTypeDto))
                .orElseGet(() -> create(contractTypeDto));
    }

    /**
     * Updates the given ContractType entity with the data from the provided ContractTypeDto.
     *
     * @param contractType    the existing ContractType entity to be updated
     * @param contractTypeDto the DTO containing the new data for the ContractType
     * @return the updated ContractTypeDto
     */
    private ContractTypeDto updateContractType(ContractType contractType, ContractTypeDto contractTypeDto) {
        ContractType newContractType = contractTypeDto.toEntity();
        newContractType.setId(contractType.getId());
        return saveContractType(newContractType);
    }

    /**
     * Deletes a ContractType by its ID.
     *
     * @param id the ID of the contract type to be deleted
     * @throws EntityNotFoundException if no contract type is found with the given ID
     */
    @Override
    @Transactional
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void deleteById(long id) {
        contractTypeExists(id);
        removeContractTypeFromUsers(id);
        repositoryCollector.getContractTypes().deleteById(id);
    }

    /**
     * Checks if a ContractType exists by its ID.
     *
     * @param id the ID of the contract type
     * @throws EntityNotFoundException if no contract type is found with the given ID
     */
    private void contractTypeExists(long id) {
        if (!repositoryCollector.getContractTypes().existsById(id)) {
            throw new EntityNotFoundException("Nie znaleziono typu umowy o id: " + id);
        }
    }

    /**
     * Removes the contract type from all users who have the specified contract type ID.
     *
     * @param id the ID of the contract type to be removed from users
     */
    private void removeContractTypeFromUsers(long id) {
        List<User> users = repositoryCollector.getUsers().findByContractType_Id(id);
        if (!users.isEmpty()) {
            users.forEach(user -> user.setContractType(null));
            repositoryCollector.getUsers().saveAll(users);
        }
    }
}
