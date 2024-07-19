package org.harmoniapp.harmoniwebapi.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.ShiftDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

/**
 * Service class for managing shifts.
 * Provides methods to retrieve shift information.
 */
@Service
@RequiredArgsConstructor
@ComponentScan(basePackages = {"org.harmoniapp.harmonidata"})
public class ShiftService {
    private final RepositoryCollector repositoryCollector;

    /**
     * Retrieves a ShiftDto for the shift with the specified ID.
     *
     * @param id the ID of the shift to retrieve
     * @return a ShiftDto containing the details of the shift
     * @throws IllegalArgumentException if the shift with the specified ID does not exist
     */
    public ShiftDto getShift(long id) {
        try {
            var shiftOptional = repositoryCollector.getShifts().findById(id);
            if (shiftOptional.isEmpty()) {
                throw new IllegalArgumentException();
            }
            var shift = shiftOptional.get();
            return ShiftDto.fromEntity(shift);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

}
