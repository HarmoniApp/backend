package org.harmoniapp.services.absence;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.absence.AbsenceTypeDto;
import org.harmoniapp.entities.absence.AbsenceType;
import org.harmoniapp.exception.EntityNotFound;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for managing absence types.
 */
@Service
@RequiredArgsConstructor
public class AbsenceTypeServiceImpl implements AbsenceTypeService {
    private final RepositoryCollector repositoryCollector;

    /**
     * Retrieves an absence type by its ID.
     *
     * @param id the ID of the absence type to retrieve
     * @return an AbsenceTypeDto representing the absence type
     * @throws EntityNotFound if no absence type is found with the given ID
     */
    @Override
    public AbsenceTypeDto getAbsenceType(long id) {
        AbsenceType absenceType = repositoryCollector.getAbsenceTypes().findById(id)
                .orElseThrow(() -> new EntityNotFound("Nie znaleziono typu nieobecności o id: " + id));
        return AbsenceTypeDto.fromEntity(absenceType);
    }

    /**
     * Retrieves all absence types.
     *
     * @return a list of AbsenceTypeDto representing all absence types.
     */
    @Override
    public List<AbsenceTypeDto> getAllAbsenceTypes() {
        return repositoryCollector.getAbsenceTypes().findAll()
                .stream()
                .map(AbsenceTypeDto::fromEntity)
                .toList();
    }
}
