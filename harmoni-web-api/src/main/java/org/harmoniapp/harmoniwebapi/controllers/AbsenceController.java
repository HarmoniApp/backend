package org.harmoniapp.harmoniwebapi.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.AbsenceDto;
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
     * Retrieves a list of AbsenceDto for a specific user by their ID.
     *
     * @param id the ID of the user whose absences are to be retrieved
     * @return a list of AbsenceDto corresponding to the user's absences
     */
    @GetMapping("user/{id}")
    public List<AbsenceDto> getAbsenceByUserId(@PathVariable long id){
        return absenceService.getAbsenceByUserId(id);
    }

//    /**
//     * Retrieves a list of absences for a specific user, filtered by archived status.
//     *
//     * @param id       the ID of the user whose absences are to be retrieved
//     * @param archived a boolean indicating whether to filter by archived absences
//     * @return a list of AbsenceDto objects representing the user's absences
//     */
//    @GetMapping("user/{id}/archived")
//    public List<AbsenceDto> getAbsenceByUserId(@PathVariable long id, @RequestParam boolean archived){
//        return absenceService.getAbsenceByUserIdAndArchive(id, archived);
//    }

    /**
     * Retrieves a list of Absences with the specified status name.
     *
     * @param id the id of the status to filter absences by
     * @return a list of AbsenceDto representing the absences with the specified status name
     */
    @GetMapping("status/{id}")
    public List<AbsenceDto> getAbsenceByStatus(@PathVariable long id){
        return absenceService.getAbsenceByStatus(id);
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
    public List<AbsenceDto> getAbsenceByDateRangeAndUserId(@RequestParam("startDate") LocalDate startDate, @RequestParam("endDate") LocalDate endDate, @RequestParam("userId") Long userId){
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
    public List<AbsenceDto> getApprovedAbsenceByDateRangeAndUserId(@RequestParam("startDate") LocalDate startDate, @RequestParam("endDate") LocalDate endDate, @RequestParam("userId") Long userId){
        return absenceService.getApprovedAbsenceByDateRangeAndUserId(userId, startDate, endDate);
    }

    /**
     * Retrieves a list of all Absences.
     *
     * @return a list of AbsenceDto representing all absences
     */
    @GetMapping
    public List<AbsenceDto> getAllAbsences() {
        return absenceService.getAllAbsences();
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
     * @param id       the ID of the absence to update
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
     * @param id the ID of the absence to update
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

    @DeleteMapping("/{id}")
    public void deleteAbsence(@PathVariable long id) {
        absenceService.deleteAbsence(id);
    }
}
