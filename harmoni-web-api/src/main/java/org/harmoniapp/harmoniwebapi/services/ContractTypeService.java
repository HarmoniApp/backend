package org.harmoniapp.harmoniwebapi.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.ContractType;
import org.harmoniapp.harmonidata.entities.Role;
import org.harmoniapp.harmonidata.entities.Shift;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.ContractTypeDto;
import org.harmoniapp.harmoniwebapi.contracts.RoleDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing contractType.
 * Provides methods to retrieve contractType information.
 */
@Service
@RequiredArgsConstructor
@ComponentScan(basePackages = {"org.harmoniapp.harmonidata"})
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

    public void deleteContractType(long id) {
        try {
            repositoryCollector.getContractTypes().deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

}
