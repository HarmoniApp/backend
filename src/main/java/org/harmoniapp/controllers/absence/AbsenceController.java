package org.harmoniapp.controllers.absence;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.PageDto;
import org.harmoniapp.contracts.absence.AbsenceDto;
import org.harmoniapp.services.absence.AbsenceService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing absence.
 * Provides endpoints to retrieve absence information.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/absence")
public class AbsenceController {
    private final AbsenceService absenceService;

    /**
     * Retrieves a paginated list of AbsenceDto for a specific user by their ID.
     *
     * @param id         the ID of the user whose absences are to be retrieved
     * @param pageNumber the page number to retrieve (optional, default is 1)
     * @param pageSize   the number of items per page (optional, default is 50)
     * @return a PageDto containing a list of AbsenceDto corresponding to the user's absences
     */
    @GetMapping("user/{id}")
    public PageDto<AbsenceDto> getAbsenceByUserId(@PathVariable long id,
                                                  @RequestParam(name = "pageNumber", required = false, defaultValue = "1") int pageNumber,
                                                  @RequestParam(name = "pageSize", required = false, defaultValue = "50") int pageSize) {
        return absenceService.getByUserId(id, pageNumber, pageSize);
    }

    /**
     * Retrieves a paginated list of AbsenceDto filtered by the specified status ID.
     *
     * @param id         the ID of the status to filter absences by
     * @param pageNumber the page number to retrieve (optional, default is 1)
     * @param pageSize   the number of items per page (optional, default is 50)
     * @return a PageDto containing a list of AbsenceDto representing the absences with the specified status ID
     */
    @GetMapping("status/{id}")
    public PageDto<AbsenceDto> getAbsenceByStatus(@PathVariable long id,
                                                  @RequestParam(name = "pageNumber", required = false, defaultValue = "1") int pageNumber,
                                                  @RequestParam(name = "pageSize", required = false, defaultValue = "50") int pageSize) {
        return absenceService.getByStatus(id, pageNumber, pageSize);
    }

    /**
     * Retrieves a paginated list of all absences.
     *
     * @param pageNumber the page number to retrieve (optional, default is 1)
     * @param pageSize   the number of items per page (optional, default is 50)
     * @return a PageDto containing a list of AbsenceDto representing all absences
     */
    @GetMapping
    public PageDto<AbsenceDto> getAllAbsences(@RequestParam(name = "pageNumber", required = false, defaultValue = "1") int pageNumber,
                                              @RequestParam(name = "pageSize", required = false, defaultValue = "50") int pageSize) {
        return absenceService.getAll(pageNumber, pageSize);
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
        return absenceService.create(absenceDto);
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
        return absenceService.updateStatus(id, statusId);
    }

    /**
     * Deletes an absence by its ID.
     *
     * @param id the ID of the absence to be deleted
     */
    @DeleteMapping("/{id}/status/{statusId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@securityService.isAbsenceOwner(#id, authentication)")
    public void deleteAbsence(@PathVariable long id, @PathVariable long statusId) {
        absenceService.deleteAbsence(id, statusId);
    }
}
