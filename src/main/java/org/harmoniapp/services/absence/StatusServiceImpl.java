package org.harmoniapp.services.absence;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.absence.StatusDto;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for managing status-related operations.
 */
@Service
@RequiredArgsConstructor
public class StatusServiceImpl implements StatusService {
    private final RepositoryCollector repositoryCollector;

    /**
     * Retrieves all statuses sorted by name.
     *
     * @return a list of StatusDto objects representing all statuses.
     */
    @Override
    public List<StatusDto> getAllStatuses() {
        return repositoryCollector.getStatuses()
                .findAll(Sort.by("name"))
                .stream()
                .map(StatusDto::fromEntity)
                .toList();
    }
}
