package org.harmoniapp.controllers.schedule;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.schedule.ShiftDto;
import org.harmoniapp.services.schedule.ShiftService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
     * <p>This endpoint is secured and only accessible by users with the 'ADMIN' role
     * or the owner of the shift.</p>
     *
     * @param id the ID of the shift to retrieve
     * @return a ShiftDto containing shift information
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') || @securityService.isShiftOwner(#id, authentication)")
    public ShiftDto getShift(@PathVariable long id) {
        return shiftService.getById(id);
    }

    /**
     * Retrieves a list of ShiftDto within the specified date range.
     *
     * @param start the start date and time
     * @param end   the end date and time
     * @return a list of ShiftDto within the specified date range
     */
    @GetMapping("/range")
    public List<ShiftDto> getShiftsByDateRangeAndUserId(@RequestParam("start") String start,
                                                        @RequestParam("end") String end,
                                                        @RequestParam("user_id") Long userId) {
        return shiftService.getShiftsByDateRangeAndUserId(start, end, userId);
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
        return shiftService.create(shiftDto);
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
        return shiftService.updateById(id, shiftDto);
    }

    /**
     * Publishes existing shifts within the specified date range.
     *
     * @param start the start date of the range
     * @param end   the end date of the range
     * @return a list of ShiftDto with the 'published' status set to true
     */
    @PatchMapping("/{start}/{end}")
    public List<ShiftDto> publishShifts(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
                                        @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        return shiftService.publish(start, end);
    }

    /**
     * Deletes a shift by its ID.
     *
     * @param id the ID of the shift to delete
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteShift(@PathVariable long id) {
        shiftService.deleteById(id);
    }
}
