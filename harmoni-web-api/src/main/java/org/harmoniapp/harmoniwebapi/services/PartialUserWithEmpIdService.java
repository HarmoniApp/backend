package org.harmoniapp.harmoniwebapi.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.PageDto;
import org.harmoniapp.harmoniwebapi.contracts.PartialUserWithEmpIdDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * Service class for managing user employee ID.
 * Provides methods to retrieve user information and their associated employee ID.
 */
@Service
@RequiredArgsConstructor
@ComponentScan(basePackages = {"org.harmoniapp.harmonidata"})
public class PartialUserWithEmpIdService {
    private final RepositoryCollector repositoryCollector;

    /**
     * Retrieves a paginated list of all users and maps their data to PartialUserWithEmpIdDto.
     *
     * @param pageNumber the page number to retrieve.
     * @param pageSize   the number of users per page.
     * @return a PageDto containing a list of PartialUserWithEmpIdDto objects representing partial user data, including their employee ID.
     */
    public PageDto<PartialUserWithEmpIdDto> getAllPartialUsers(int pageNumber, int pageSize) {
        pageNumber = (pageNumber < 1) ? 0 : pageNumber-1;
        pageSize = (pageSize < 1) ? 10 : pageSize;

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("firstname", "surname"));
        Page<User> users = repositoryCollector.getUsers().findAllByIsActive(true, pageable);

        return new PageDto<>(users.stream().map(PartialUserWithEmpIdDto::fromEntity).toList(),
                users.getSize(),
                users.getNumber()+1,
                users.getTotalPages());
    }
}
