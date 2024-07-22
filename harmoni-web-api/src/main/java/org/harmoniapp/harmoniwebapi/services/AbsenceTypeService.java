package org.harmoniapp.harmoniwebapi.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.AbsenceType;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.AbsenceTypeDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing absenceType.
 * Provides methods to retrieve absenceType information.
 */
@Service
@RequiredArgsConstructor
@ComponentScan(basePackages = {"org.harmoniapp.harmonidata"})
public class AbsenceTypeService {
    private final RepositoryCollector repositoryCollector;

    /**
     * Retrieves a AbsenceTypeDto for the absenceType with the specified ID.
     *
     * @param id the ID of the absenceType to retrieve
     * @return a AbsenceTypeDto containing the details of the absenceType
     * @throws IllegalArgumentException if the absenceType with the specified ID does not exist
     */
    public AbsenceTypeDto getAbsenceType(long id) {
        try {
            AbsenceType absenceType = repositoryCollector.getAbsenceTypes().findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("AbsenceType with ID " + id + " not found"));

            return AbsenceTypeDto.fromEntity(absenceType);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a list of all AbsenceTypeDto.
     *
     * @return a list of AbsenceTypeDto containing the details of all absenceType
     * @throws RuntimeException if there is an error
     */
    public List<AbsenceTypeDto> getAllAbsenceTypes() {
        try {
            var absenceType = repositoryCollector.getAbsenceTypes().findAll();
            return absenceType.stream()
                    .map(AbsenceTypeDto::fromEntity)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

}
