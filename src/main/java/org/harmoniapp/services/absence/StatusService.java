package org.harmoniapp.services.absence;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.absence.StatusDto;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing statuses.
 * Provides methods to retrieve status information.
 */
@Service
@RequiredArgsConstructor
public class StatusService {
    private final RepositoryCollector repositoryCollector;

    /**
     * Retrieves a list of all StatusSto.
     *
     * @return a list of StatusDto containing the details of all statuses
     * @throws RuntimeException if an error occurs while retrieving statuses
     */
    public List<StatusDto> getAllStatuses(){
        try {
            var status = repositoryCollector.getStatuses().findAll(Sort.by("name"));
            return status.stream()
                    .map(StatusDto::fromEntity)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }
}
