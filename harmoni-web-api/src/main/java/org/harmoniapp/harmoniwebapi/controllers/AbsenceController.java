package org.harmoniapp.harmoniwebapi.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.AbsenceDto;
import org.harmoniapp.harmoniwebapi.contracts.PageDto;
import org.harmoniapp.harmoniwebapi.services.AbsenceService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for managing absence.
 * Provides endpoints to retrieve absence information.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/absence")
@CrossOrigin(origins = "http://localhost:3000")
public class AbsenceController {
    private final AbsenceService absenceService;

    /**
     * Retrieves a paginated list of AbsenceDto for a specific user by their ID.
     *
     * @param id         the ID of the user whose absences are to be retrieved
     * @param pageNumber the page number to retrieve (optional, default is 0)
     * @param pageSize   the number of items per page (optional, default is 50)
     * @return a PageDto containing a list of AbsenceDto corresponding to the user's absences
     */
    @GetMapping("user/{id}")
    public PageDto<AbsenceDto> getAbsenceByUserId(@PathVariable long id,
                                                  @RequestParam(name = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                                  @RequestParam(name = "pageSize", required = false, defaultValue = "50") int pageSize) {
        return absenceService.getAbsenceByUserId(id, pageNumber, pageSize);
    }

    /**
     * Retrieves a paginated list of absences for a specific user, filtered by archived status.
     *
     * @param id         the ID of the user whose absences are to be retrieved
     * @param archived   a boolean indicating whether to filter by archived absences
     * @param pageNumber the page number to retrieve (optional, default is 0)
     * @param pageSize   the number of items per page (optional, default is 50)
     * @return a PageDto containing a list of AbsenceDto objects representing the user's absences
     */
    @GetMapping("user/{id}/archived")
    public PageDto<AbsenceDto> getAbsenceByUserId(@PathVariable long id,
                                                  @RequestParam boolean archived,
                                                  @RequestParam(name = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                                  @RequestParam(name = "pageSize", required = false, defaultValue = "50") int pageSize) {
        return absenceService.getAbsenceByUserIdAndArchive(id, archived, pageNumber, pageSize);
    }

    /**
     * Retrieves a paginated list of AbsenceDto filtered by the specified status ID.
     *
     * @param id         the ID of the status to filter absences by
     * @param pageNumber the page number to retrieve (optional, default is 0)
     * @param pageSize   the number of items per page (optional, default is 50)
     * @return a PageDto containing a list of AbsenceDto representing the absences with the specified status ID
     */
    @GetMapping("status/{id}")
    public PageDto<AbsenceDto> getAbsenceByStatus(@PathVariable long id,
                                                  @RequestParam(name = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                                  @RequestParam(name = "pageSize", required = false, defaultValue = "50") int pageSize) {
        return absenceService.getAbsenceByStatus(id, pageNumber, pageSize);
    }

    /**
     * Retrieves a list of approved absences for a specified user within a given date range.
     *
     * @param startDate the start date of the range to filter absences (format: yyyy-MM-dd)
     * @param endDate   the end date of the range to filter absences (format: yyyy-MM-dd)
     * @param userId    the ID of the user to filter absences for
     * @return a list of AbsenceDto representing the approved absences within the specified date range for the given user
     */
    @GetMapping("/range/user")
    public List<AbsenceDto> getAbsenceByDateRangeAndUserId(@RequestParam("startDate") LocalDate startDate,
                                                           @RequestParam("endDate") LocalDate endDate,
                                                           @RequestParam("userId") Long userId) {
        return absenceService.getAbsenceByDateRangeAndUserId(userId, startDate, endDate);
    }

    /**
     * Retrieves a list of approved absences for a specified user within a given date range.
     *
     * @param startDate the start date of the range to filter absences (format: yyyy-MM-dd)
     * @param endDate   the end date of the range to filter absences (format: yyyy-MM-dd)
     * @param userId    the ID of the user to filter absences for
     * @return a list of AbsenceDto representing the approved absences within the specified date range for the given user
     */
    @GetMapping("/range/onlyApproved")
    public List<AbsenceDto> getApprovedAbsenceByDateRangeAndUserId(@RequestParam("startDate") LocalDate startDate,
                                                                   @RequestParam("endDate") LocalDate endDate,
                                                                   @RequestParam("userId") Long userId) {
        return absenceService.getApprovedAbsenceByDateRangeAndUserId(userId, startDate, endDate);
    }

    /**
     * Retrieves a paginated list of all absences.
     *
     * @param pageNumber the page number to retrieve (optional, default is 0)
     * @param pageSize   the number of items per page (optional, default is 50)
     * @return a PageDto containing a list of AbsenceDto representing all absences
     */
    @GetMapping
    public PageDto<AbsenceDto> getAllAbsences(@RequestParam(name = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                              @RequestParam(name = "pageSize", required = false, defaultValue = "50") int pageSize) {
        return absenceService.getAllAbsences(pageNumber, pageSize);
    }

    /**
     * Creates a new Absence.
     *
     * @param absenceDto the AbsenceDto containing the details of the absence to create
     * @return the created AbsenceDto
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AbsenceDto createAbsence(@Valid @RequestBody AbsenceDto absenceDto) {
        return absenceService.createAbsence(absenceDto);
    }

    /**
     * Updates an existing Absence or creates a new one if it doesn't exist.
     *
     * @param id         the ID of the absence to update
     * @param absenceDto the AbsenceDto containing the details of the absence to update
     * @return the updated or newly created AbsenceDto
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public AbsenceDto updateAbsence(@PathVariable long id, @Valid @RequestBody AbsenceDto absenceDto) {
        return absenceService.updateAbsence(id, absenceDto);
    }

    /**
     * Updates the status of an existing Absence identified by its ID.
     *
     * @param id       the ID of the absence to update
     * @param statusId the ID of the new status to set for the absence
     * @return the updated AbsenceDto
     */
    @PatchMapping("/{id}/status/{statusId}")
    @ResponseStatus(HttpStatus.CREATED)
    public AbsenceDto updateAbsenceStatus(@PathVariable long id, @PathVariable long statusId) {
        return absenceService.updateAbsenceStatus(id, statusId);
    }

    /**
     * Updates the archived status of an existing Absence identified by its ID.
     *
     * @param id       the ID of the absence to be updated
     * @param archived a boolean indicating the new archived status
     * @return the updated AbsenceDto object representing the absence with the new archived status
     */
    @PatchMapping("/archive/{id}")
    public AbsenceDto updateAbsenceArchived(@PathVariable long id, @RequestParam boolean archived) {
        return absenceService.updateAbsenceArchived(id, archived);
    }

}
