package org.harmoniapp.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.entities.User;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.contracts.PageDto;
import org.harmoniapp.contracts.SupervisorDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
     * Retrieves a paginated list of all supervisors.
     * It fetches supervisors from the repository and converts them to {@link SupervisorDto}.
     *
     * @param pageNumber the page number to retrieve.
     * @param pageSize   the number of supervisors per page.
     * @return a PageDto containing a list of {@link SupervisorDto} representing all supervisors.
     */
    public PageDto<SupervisorDto> getAllSupervisors(int pageNumber, int pageSize) {
        pageNumber = (pageNumber < 1) ? 0 : pageNumber-1;
        pageSize = (pageSize < 1) ? 20 : pageSize;

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("firstname", "surname"));
        Page<User> users = repositories.getUsers().findSupervisors(pageable);

        return new PageDto<>(users.stream().map(SupervisorDto::fromEntity).toList(),
                users.getSize(),
                users.getNumber()+1,
                users.getTotalPages());
    }
}