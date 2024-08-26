package org.harmoniapp.harmoniwebapi.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.StatusDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing statuses.
 * Provides methods to retrieve status information.
 */
@Service
@RequiredArgsConstructor
@ComponentScan(basePackages = {"org.harmoniapp.harmonidata"})
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
