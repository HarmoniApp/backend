package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.ContractTypeDto;
import org.harmoniapp.harmoniwebapi.services.ContractTypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing contractType.
 * Provides endpoints to retrieve contractType information.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/contract-type")
public class ContractTypeController {
    private final ContractTypeService contractTypeService;

    /**
     * Retrieves a contractType information by ID.
     *
     * @param id the ID of the contractType to retrieve
     * @return a ContractTypeDto containing contractType information
     */
    @GetMapping("/{id}")
    public ContractTypeDto getContractType(@PathVariable long id) {
        return contractTypeService.getContractType(id);
    }

    /**
     * Retrieves a list of all ContractTypeDto.
     *
     * @return a list of ContractTypeDto containing the details of all contractType
     */
    @GetMapping
    public List<ContractTypeDto> getAllContractTypes() {
        return contractTypeService.getAllContractTypes();
    }

}
