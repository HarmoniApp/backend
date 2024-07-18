package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.ShiftDto;
import org.harmoniapp.harmoniwebapi.services.ShiftService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing shift.
 * Provides endpoints to retrieve shift information.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/shift")
public class ShiftController {
    private final ShiftService shiftService;

    /**
     * Retrieves a shift's information by shift ID.
     *
     * @param id the ID of the shift to retrieve
     * @return a ShiftDto containing shift information
     */
    @GetMapping("/{id}")
    public ShiftDto getShift(@PathVariable long id) {
        return shiftService.getShift(id);
    }

}
