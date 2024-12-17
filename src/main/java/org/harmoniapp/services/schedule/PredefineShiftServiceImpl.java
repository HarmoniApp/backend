package org.harmoniapp.services.schedule;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.schedule.PredefineShiftDto;
import org.harmoniapp.entities.schedule.PredefineShift;
import org.harmoniapp.exception.EntityNotFound;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the PredefineShiftService interface.
 * This service provides methods to manage predefined shifts.
 */
@Service
@RequiredArgsConstructor
public class PredefineShiftServiceImpl implements PredefineShiftService {
    private final RepositoryCollector repositoryCollector;

    /**
     * Retrieves a PredefineShiftDto for the predefined shift with the specified ID.
     *
     * @param id the ID of the predefined shift to retrieve
     * @return a PredefineShiftDto containing the details of the predefined shift
     * @throws EntityNotFound if the predefined shift with the specified ID does not exist
     */
    public PredefineShiftDto get(long id) {
        return repositoryCollector.getPredefineShifts().findById(id)
                .map(PredefineShiftDto::fromEntity)
                .orElseThrow(() -> new EntityNotFound("Nie znaleziono predefiniowanej zmiany o podanym id: %d".formatted(id)));
    }

    /**
     * Retrieves all predefined shifts from the database.
     *
     * @return a list of PredefineShiftDto containing the details of all predefined shifts
     */
    public List<PredefineShiftDto> getAll() {
        return repositoryCollector.getPredefineShifts().findAll(Sort.by(Sort.Direction.ASC, "name"))
                .stream()
                .map(PredefineShiftDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new predefined shift in the database.
     *
     * @param predefineShiftDto the DTO containing the details of the predefined shift to create
     * @return the newly created PredefineShiftDto
     * @throws RuntimeException if there is an error accessing the database or the provided data is invalid
     */
    @Transactional
    public PredefineShiftDto create(PredefineShiftDto predefineShiftDto) {
        PredefineShift predefineShift = predefineShiftDto.toEntity();
        PredefineShift savedPredefineShift = repositoryCollector.getPredefineShifts().save(predefineShift);
        return PredefineShiftDto.fromEntity(savedPredefineShift);
    }

    /**
     * Updates an existing predefined shift in the database or creates a new one if the ID does not exist.
     *
     * @param id                the ID of the predefined shift to update or create
     * @param predefineShiftDto the DTO containing the details of the predefined shift to update or create
     * @return the updated or newly created PredefineShiftDto
     * @throws RuntimeException if there is an error accessing the database or the provided data is invalid
     */
    @Transactional
    public PredefineShiftDto update(long id, PredefineShiftDto predefineShiftDto) {
        return repositoryCollector.getPredefineShifts().findById(id)
                .map(predefineShift -> updateExistingShift(predefineShift, predefineShiftDto))
                .orElseGet(() -> create(predefineShiftDto));
    }

    /**
     * Updates an existing predefined shift with new details.
     *
     * @param predefineShift    the existing predefined shift entity
     * @param predefineShiftDto the DTO containing the new details for the predefined shift
     * @return the updated PredefineShiftDto
     */
    private PredefineShiftDto updateExistingShift(PredefineShift predefineShift, PredefineShiftDto predefineShiftDto) {
        PredefineShift newPredefineShift = predefineShiftDto.toEntity();
        newPredefineShift.setId(predefineShift.getId());
        PredefineShift updatedPredefineShift = repositoryCollector.getPredefineShifts().save(newPredefineShift);
        return PredefineShiftDto.fromEntity(updatedPredefineShift);
    }

    /**
     * Deletes a predefined shift by its ID.
     *
     * @param id the ID of the predefined shift to delete
     * @throws RuntimeException if an error occurs during deletion
     */
    @Transactional
    public void delete(long id) {
        repositoryCollector.getPredefineShifts().deleteById(id);
    }
}
