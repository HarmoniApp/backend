package org.harmoniapp.harmoniwebapi.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.*;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.AbsenceDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing absences.
 * Provides methods to retrieve absences information.
 */
@Service
@RequiredArgsConstructor
@ComponentScan(basePackages = {"org.harmoniapp.harmonidata"})
public class AbsenceService {
    private final RepositoryCollector repositoryCollector;

    /**
     * Retrieves a list of all absences.
     *
     * @return a list of AbsenceDto containing the details of all absences
     * @throws RuntimeException if an error occurs while retrieving absences
     */
    public List<AbsenceDto> getAllAbsences() {
        try {
            var absence = repositoryCollector.getAbsences().findAll();
            return absence.stream()
                    .map(AbsenceDto::fromEntity)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

    /**
     * Creates a new Absence.
     *
     * @param absenceDto the AbsenceDto containing the details of the absence to create
     * @return the created AbsenceDto
     * @throws IllegalArgumentException if the user or absence type ID provided does not exist
     * @throws RuntimeException if an error occurs during creation
     */
    public AbsenceDto createAbsence(AbsenceDto absenceDto) {

        User user = repositoryCollector.getUsers()
                .findById(absenceDto.userId())
                .orElseThrow(IllegalArgumentException::new);

        AbsenceType absenceType = repositoryCollector.getAbsenceTypes()
                .findById(absenceDto.absenceTypeId())
                .orElseThrow(IllegalArgumentException::new);

        Status status = repositoryCollector.getStatuses()
                .findById(1L)    //Here status MUST be awaiting
                .orElseThrow(IllegalArgumentException::new);

        Absence absence = absenceDto.toEntity(user, absenceType, status);
        Absence savedAbsence = repositoryCollector.getAbsences().save(absence);
        return AbsenceDto.fromEntity(savedAbsence);
    }
}
