package org.harmoniapp.harmoniwebapi.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.SupervisorDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing supervisors.
 * This service provides methods to retrieve supervisors from the repository.
 */
@Service
@RequiredArgsConstructor
@ComponentScan(basePackages = {"org.harmoniapp.harmonidata"})
public class SupervisorService {
    private final RepositoryCollector repositories;

    /**
     * Retrieves all supervisors.
     * It fetches supervisors from the repository and converts them to {@link SupervisorDto}.
     *
     * @return a list of {@link SupervisorDto} representing all supervisors.
     */
    public List<SupervisorDto> getAllSupervisors(){
        List<User> users = repositories.getUsers().findSupervisors();
        return users.stream()
                .map(SupervisorDto::fromEntity)
                .toList();
    }
}