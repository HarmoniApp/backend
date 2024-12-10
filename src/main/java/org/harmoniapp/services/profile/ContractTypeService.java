package org.harmoniapp.services.profile;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.profile.ContractTypeDto;
import org.harmoniapp.entities.profile.ContractType;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * Service class for managing contractType.
 * Provides methods to retrieve contractType information.
 */
@Service
@RequiredArgsConstructor
public class ContractTypeService {
    private final RepositoryCollector repositoryCollector;

    /**
     * Retrieves a ContractTypeDto for the contractType with the specified ID.
     *
     * @param id the ID of the contractType to retrieve
     * @return a ContractTypeDto containing the details of the contractType
     * @throws IllegalArgumentException if the contractType with the specified ID does not exist
     */
    public ContractTypeDto getContractType(long id) {
        try {
            ContractType contractType = repositoryCollector.getContractTypes().findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ContractType with ID " + id + " not found"));

            return ContractTypeDto.fromEntity(contractType);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a list of all ContractTypeDto.
     *
     * @return a list of ContractTypeDto containing the details of all contractType
     * @throws RuntimeException if there is an error
     */
    public List<ContractTypeDto> getAllContractTypes() {
        try {
            var contractTypes = repositoryCollector.getContractTypes().findAll();
            return contractTypes.stream()
                    .map(ContractTypeDto::fromEntity)
                    .sorted(Comparator.comparing(ContractTypeDto::name))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

    public ContractTypeDto createContractType(ContractTypeDto contractTypeDto) {
        try {
            ContractType contractType = contractTypeDto.toEntity();
            ContractType contractTypeSaved = repositoryCollector.getContractTypes().save(contractType);
            return ContractTypeDto.fromEntity(contractTypeSaved);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

    public ContractTypeDto updateContractType(long id, ContractTypeDto contractTypeDto) {
        try {
            ContractType newContractType = contractTypeDto.toEntity();
            return repositoryCollector.getContractTypes().findById(id)
                    .map(contractType -> {
                        contractType.setName(newContractType.getName());
                        contractType.setAbsenceDays(newContractType.getAbsenceDays());
                        ContractType updatedContractType = repositoryCollector.getContractTypes().save(contractType);
                        return ContractTypeDto.fromEntity(updatedContractType);
                    })
                    .orElseGet(() -> {
                        ContractType contractType = repositoryCollector.getContractTypes().save(newContractType);
                        return ContractTypeDto.fromEntity(contractType);
                    });
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a contract type by its ID.
     * If the contract type is associated with any users, it removes the contract type from those users before deleting it.
     *
     * @param id the ID of the contract type to delete
     * @throws IllegalArgumentException if the contract type with the specified ID does not exist
     */
    public void deleteContractType(long id) {
        if (!repositoryCollector.getContractTypes().existsById(id)) {
            throw new IllegalArgumentException("ContractType with ID " + id + " not found");
        }
        var users = repositoryCollector.getUsers().findByContractType_Id(id);
        if (!users.isEmpty()) {
            users.forEach(user -> user.setContractType(null));
            repositoryCollector.getUsers().saveAll(users);
        }

        repositoryCollector.getContractTypes().deleteById(id);
    }
}
