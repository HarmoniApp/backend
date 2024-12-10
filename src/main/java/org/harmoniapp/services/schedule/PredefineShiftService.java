package org.harmoniapp.services.schedule;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.schedule.PredefineShiftDto;
import org.harmoniapp.entities.schedule.PredefineShift;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing predefineShift.
 * Provides methods to retrieve predefineShift information.
 */
@Service
@RequiredArgsConstructor
public class PredefineShiftService {
    private final RepositoryCollector repositoryCollector;

    /**
     * Retrieves a PredefineShiftDto for the predefined shift with the specified ID.
     *
     * @param id the ID of the predefined shift to retrieve
     * @return a PredefineShiftDto containing the details of the predefined shift
     * @throws IllegalArgumentException if the predefined shift with the specified ID does not exist
     */
    public PredefineShiftDto getPredefineShift(long id) {
        try {
            PredefineShift predefineShift = repositoryCollector.getPredefineShifts().findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("PredefineShift with ID " + id + " not found"));

            return PredefineShiftDto.fromEntity(predefineShift);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a list of all PredefineShiftDto.
     *
     * @return a list of PredefineShiftDto containing the details of all predefined shifts
     * @throws RuntimeException if there is an error
     */
    public List<PredefineShiftDto> getAllPredefineShifts() {
        try {
            var predefineShifts = repositoryCollector.getPredefineShifts().findAll(Sort.by(Sort.Direction.ASC, "name"));
            return predefineShifts.stream()
                    .map(PredefineShiftDto::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

    /**
     * Saves a new predefined shift to the database.
     *
     * @param predefineShiftDto the DTO containing the details of the predefined shift to save
     * @return the saved PredefineShiftDto
     * @throws RuntimeException if there is any error during the process
     */
    public PredefineShiftDto createPredefineShift(PredefineShiftDto predefineShiftDto) {
        try {
            PredefineShift predefineShift = predefineShiftDto.toEntity();
            PredefineShift savedPredefineShift = repositoryCollector.getPredefineShifts().save(predefineShift);
            return PredefineShiftDto.fromEntity(savedPredefineShift);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing predefined shift in the database or creates a new one if the ID does not exist.
     *
     * @param id the ID of the predefined shift to update or create
     * @param predefineShiftDto the DTO containing the details of the predefined shift to update or create
     * @return the updated or newly created PredefineShiftDto
     * @throws RuntimeException if there is an error accessing the database or the provided data is invalid
     */
    @Transactional
    public PredefineShiftDto updatePredefineShift(long id, PredefineShiftDto predefineShiftDto) {
        try {
            PredefineShift newPredefineShift = predefineShiftDto.toEntity();
            return repositoryCollector.getPredefineShifts().findById(id)
              .map(predefineShift -> {
                  predefineShift.setName(newPredefineShift.getName());
                  predefineShift.setStart(newPredefineShift.getStart());
                  predefineShift.setEnd(newPredefineShift.getEnd());
                  PredefineShift updatedPredefineShift = repositoryCollector.getPredefineShifts().save(predefineShift);
                  return PredefineShiftDto.fromEntity(updatedPredefineShift);
              })
              .orElseGet(() -> {
                  PredefineShift createdPredefineShift = repositoryCollector.getPredefineShifts().save(newPredefineShift);
                  return PredefineShiftDto.fromEntity(createdPredefineShift);
              });
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a predefined shift by its ID.
     *
     * @param id the ID of the predefined shift to delete
     * @throws RuntimeException if an error occurs during deletion
     */
    public void deletePredefineShift(long id) {
        try {
            repositoryCollector.getPredefineShifts().deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }
}
