package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.ShiftDto;
import org.harmoniapp.harmoniwebapi.contracts.UserLanguageDto;
import org.harmoniapp.harmoniwebapi.services.ShiftService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

//    /**
//     * Retrieves a paginated list of shifts.
//     *
//     * @param page the page number to retrieve (optional, default is 0)
//     * @return a list of ShiftDto containing the details of shifts for the specified page
//     */
//    @GetMapping("")
//    public List<ShiftDto> getShifts(@RequestParam(required = false, defaultValue = "0") int page) {
//        return shiftService.getShiftPage(page);
//    }

    /**
     * Deletes a shift by its ID.
     *
     * @param id the ID of the shift to delete
     */
    @DeleteMapping("/{id}")
    public void deleteShift(@PathVariable long id) {
        shiftService.deleteShift(id);
    }
}
