package org.harmoniapp.harmoniwebapi.services;

import lombok.RequiredArgsConstructor;
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
        var predefineShiftOptional = repositoryCollector.getPredefineShifts().findById(id);

        if (predefineShiftOptional.isEmpty()) {
            throw new IllegalArgumentException();
        }

        var predefineShift = predefineShiftOptional.get();

        return PredefineShiftDto.toDto(predefineShift);
    }

    /**
     * Retrieves a list of all PredefineShiftDto.
     *
     * @return a list of PredefineShiftDto containing the details of all predefined shifts
     */
    public List<PredefineShiftDto> getAllPredefineShifts() {
        var predefineShifts = repositoryCollector.getPredefineShifts().findAll();
        return predefineShifts.stream()
                .map(PredefineShiftDto::toDto)
                .collect(Collectors.toList());
    }
}
