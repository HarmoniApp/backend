package org.harmoniapp.controllers.profile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.profile.ContractTypeDto;
import org.harmoniapp.services.profile.ContractTypeServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing contractType.
 * Provides endpoints to retrieve contractType information.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/contract-type")
public class ContractTypeController {
    private final ContractTypeServiceImpl contractTypeService;

    /**
     * Retrieves a contractType information by ID.
     *
     * @param id the ID of the contractType to retrieve
     * @return a ContractTypeDto containing contractType information
     */
    @GetMapping("/{id}")
    public ContractTypeDto getContractType(@PathVariable long id) {
        return contractTypeService.getById(id);
    }

    /**
     * Retrieves a list of all ContractTypeDto.
     *
     * @return a list of ContractTypeDto containing the details of all contractType
     */
    @GetMapping
    public List<ContractTypeDto> getAllContractTypes() {
        return contractTypeService.getAll();
    }

    /**
     * Creates a new contractType.
     *
     * @param contractTypeDto the contractType data to create
     * @return the created ContractTypeDto
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContractTypeDto createContractType(@Valid @RequestBody ContractTypeDto contractTypeDto) {
        return contractTypeService.create(contractTypeDto);
    }

    /**
     * Updates an existing contractType by ID.
     *
     * @param id              the ID of the contractType to update
     * @param contractTypeDto the contractType data to update
     * @return the updated ContractTypeDto
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ContractTypeDto updateContractType(@PathVariable long id, @Valid @RequestBody ContractTypeDto contractTypeDto) {
        return contractTypeService.updateById(id, contractTypeDto);
    }

    /**
     * Deletes a contractType by ID.
     *
     * @param id the ID of the contractType to delete
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteContractType(@PathVariable long id) {
        contractTypeService.deleteById(id);
    }
}
