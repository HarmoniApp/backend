package org.harmoniapp.harmoniwebapi.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.ContractType;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.ContractTypeDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

}
