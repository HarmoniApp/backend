package org.harmoniapp.services.user;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.PageDto;
import org.harmoniapp.contracts.PageRequestDto;
import org.harmoniapp.contracts.user.SupervisorDto;
import org.harmoniapp.entities.user.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * Service implementation for managing supervisors.
 */
@Service
@RequiredArgsConstructor
public class SupervisorServiceImpl implements SupervisorService {
    private final UserSearchService userSearchService;

    /**
     * Retrieves a paginated list of all supervisors.
     * It fetches supervisors from the repository and converts them to {@link SupervisorDto}.
     *
     * @param pageRequest the page request containing page number and size.
     * @return a PageDto containing a list of {@link SupervisorDto} representing all supervisors.
     */
    public PageDto<SupervisorDto> getAllSupervisors(PageRequestDto pageRequest) {
        Page<User> users = userSearchService.findUsersPage(pageRequest);
        return PageDto.mapPage(users, SupervisorDto::fromEntity);
    }
}