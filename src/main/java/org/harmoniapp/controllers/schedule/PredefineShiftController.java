package org.harmoniapp.controllers.schedule;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.schedule.PredefineShiftDto;
import org.harmoniapp.services.schedule.PredefineShiftService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing predefineShift.
 * Provides endpoints to retrieve, create, update, and delete predefineShift information.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/predefine-shift")
public class PredefineShiftController {
    private final PredefineShiftService predefineShiftService;

    /**
     * Retrieves a predefine shift's information by shift ID.
     *
     * @param id the ID of the predefine shift to retrieve
     * @return a PredefineShiftDto containing predefine shift information
     */
    @GetMapping("/{id}")
    public PredefineShiftDto getPredefineShift(@PathVariable long id) {
        return predefineShiftService.get(id);
    }

    /**
     * Retrieves a list of all PredefineShiftDto.
     *
     * @return a list of PredefineShiftDto containing the details of all predefined shifts
     */
    @GetMapping
    public List<PredefineShiftDto> getAllPredefineShifts() {
        return predefineShiftService.getAll();
    }

    /**
     * Creates a new predefined shift.
     *
     * @param predefineShiftDto the DTO containing the details of the predefined shift to save
     * @return the saved PredefineShiftDto
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PredefineShiftDto createPredefineShift(@Valid @RequestBody PredefineShiftDto predefineShiftDto) {
        return predefineShiftService.create(predefineShiftDto);
    }

    /**
     * Updates an existing predefined shift.
     *
     * @param predefineShiftDto the DTO containing the details of the predefined shift to update
     * @return the updated PredefineShiftDto
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public PredefineShiftDto updatePredefineShift(@PathVariable long id, @Valid @RequestBody PredefineShiftDto predefineShiftDto) {
        return predefineShiftService.update(id, predefineShiftDto);
    }

    /**
     * Deletes a predefined shift by its ID.
     *
     * @param id the ID of the predefined shift to delete
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePredefineShift(@PathVariable long id) {
        predefineShiftService.delete(id);
    }
}
