package org.harmoniapp.controllers.profile;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.profile.ContractTypeDto;
import org.harmoniapp.services.profile.ContractTypeService;
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

    @PostMapping
    public ContractTypeDto createContractType(@RequestBody ContractTypeDto contractTypeDto) {
        return contractTypeService.createContractType(contractTypeDto);
    }

    @PutMapping("/{id}")
    public ContractTypeDto updateContractType(@PathVariable long id, @RequestBody ContractTypeDto contractTypeDto) {
        return contractTypeService.updateContractType(id, contractTypeDto);
    }

    @DeleteMapping("/{id}")
    public void deleteContractType(@PathVariable long id) {
        contractTypeService.deleteContractType(id);
    }

}