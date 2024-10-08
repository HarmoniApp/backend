package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.ShiftDto;
import org.harmoniapp.harmoniwebapi.services.ShiftService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for managing shift.
 * Provides endpoints to retrieve shift information.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/shift")
@CrossOrigin(origins = "http://localhost:3000")
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

    /**
     * Retrieves a list of ShiftDto within the specified date range.
     *
     * @param start the start date and time
     * @param end   the end date and time
     * @return a list of ShiftDto within the specified date range
     */
    @GetMapping("/range")
    public List<ShiftDto> getShiftsByDateRangeAndUserId(@RequestParam("start") String start, @RequestParam("end") String end, @RequestParam("user_id") Long userId) {
        LocalDateTime startDate = LocalDateTime.parse(start);
        LocalDateTime endDate = LocalDateTime.parse(end);
        return shiftService.getShiftsByDateRangeAndUserId(startDate, endDate, userId);
    }

    /**
     * Creates a new Shift.
     *
     * @param shiftDto the ShiftDto containing the details of the shift to create
     * @return the created ShiftDto
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShiftDto addShift(@RequestBody ShiftDto shiftDto) {
        return shiftService.createShift(shiftDto);
    }

    /**
     * Updates an existing Shift or creates a new one if it doesn't exist.
     *
     * @param id       the ID of the shift to update
     * @param shiftDto the ShiftDto containing the details of the shift to update
     * @return the updated or newly created ShiftDto
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ShiftDto updateShift(@PathVariable long id, @RequestBody ShiftDto shiftDto) {
        return shiftService.updateShift(id, shiftDto);
    }

    /**
     * Publishes an existing shift by its ID.
     *
     * @param id the ID of the shift to publish
     * @return the updated ShiftDto with the 'published' status set to true
     */
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ShiftDto publishShift(@PathVariable long id) {
        return shiftService.publishShift(id);
    }

    /**
     * Deletes a shift by its ID.
     *
     * @param id the ID of the shift to delete
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteShift(@PathVariable long id) {
        shiftService.deleteShift(id);
    }
}
