package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.PredefineShiftDto;
import org.harmoniapp.harmoniwebapi.contracts.ShiftDto;
import org.harmoniapp.harmoniwebapi.services.PredefineShiftService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing predefineShift.
 * Provides endpoints to retrieve predefineShift information.
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
        return predefineShiftService.getPredefineShift(id);
    }

    /**
     * Retrieves a list of all PredefineShiftDto.
     *
     * @return a list of PredefineShiftDto containing the details of all predefined shifts
     */
    @GetMapping("/all")
    public List<PredefineShiftDto> getAllPredefineShifts() {
        return predefineShiftService.getAllPredefineShifts();
    }
}
