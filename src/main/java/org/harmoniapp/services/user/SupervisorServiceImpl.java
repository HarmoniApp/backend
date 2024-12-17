package org.harmoniapp.services.user;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.PageDto;
import org.harmoniapp.contracts.PageRequestDto;
import org.harmoniapp.contracts.user.SupervisorDto;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service implementation for managing supervisors.
 */
@Service
@RequiredArgsConstructor
public class SupervisorServiceImpl implements SupervisorService {
    private final RepositoryCollector repositoryCollector;
    private final UserSearchService userSearchService;

    /**
     * Retrieves a paginated list of all supervisors.
     *
     * @param pageRequest the page request containing page number and size.
     * @return a PageDto containing a list of {@link SupervisorDto} representing all supervisors.
     */
    public PageDto<SupervisorDto> getAllSupervisors(PageRequestDto pageRequest) {
        Pageable pageable = userSearchService.createPageable(pageRequest);
        Page<User> users = repositoryCollector.getUsers().findSupervisors(pageable);
        return PageDto.mapPage(users, SupervisorDto::fromEntity);
    }
}