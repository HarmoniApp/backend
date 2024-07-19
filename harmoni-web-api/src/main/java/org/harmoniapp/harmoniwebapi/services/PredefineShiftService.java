package org.harmoniapp.harmoniwebapi.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.PredefineShift;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.PredefineShiftDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing predefineShift.
 * Provides methods to retrieve predefineShift information.
 */
@Service
@RequiredArgsConstructor
@ComponentScan(basePackages = {"org.harmoniapp.harmonidata"})
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
            var predefineShiftOptional = repositoryCollector.getPredefineShifts().findById(id);
            if (predefineShiftOptional.isEmpty()) {
                throw new IllegalArgumentException("PredefineShift with ID " + id + " not found");
            }
            var predefineShift = predefineShiftOptional.get();
            return PredefineShiftDto.fromEntity(predefineShift);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a list of all PredefineShiftDto.
     *
     * @return a list of PredefineShiftDto containing the details of all predefined shifts
     * @throws RuntimeException if there is an error accessing the database
     */
    public List<PredefineShiftDto> getAllPredefineShifts() {
        try {
            var predefineShifts = repositoryCollector.getPredefineShifts().findAll();
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
     * Updates an existing predefined shift in the database.
     *
     * @param predefineShiftDto the DTO containing the details of the predefined shift to update
     * @return the updated PredefineShiftDto
     * @throws RuntimeException if there is an error accessing the database or the provided data is invalid
     */
    public PredefineShiftDto updatePredefineShift(PredefineShiftDto predefineShiftDto) {
        try {
            if (repositoryCollector.getPredefineShifts().existsById(predefineShiftDto.id())) {
                PredefineShift predefineShift = predefineShiftDto.toEntity();
                PredefineShift updatedPredefineShift = repositoryCollector.getPredefineShifts().save(predefineShift);
                return PredefineShiftDto.fromEntity(updatedPredefineShift);
            } else {
                throw new RuntimeException("PredefineShift with ID " + predefineShiftDto.id() + " does not exist.");
            }
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
