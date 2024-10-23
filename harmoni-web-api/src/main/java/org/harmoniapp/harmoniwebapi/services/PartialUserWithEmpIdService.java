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

import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * Searches for users based on a query string. The query string is split into individual words
     * and used to match against user attributes. If the query string is empty or null, an exception is thrown.
     *
     * @param q the search query string to filter users by. This can match against various user attributes.
     * @return a list of {@link PartialUserWithEmpIdDto} objects that match the search query.
     * @throws IllegalArgumentException if the query string is null or empty.
     */
    public List<PartialUserWithEmpIdDto> getUsersSearch(String q) {
        if (q == null || q.isEmpty()) {
            throw new IllegalArgumentException();
        }

        q = q.toUpperCase();
        List<String> qSplit = List.of(q.split(" "));

        List<User> users;
        if (qSplit.size() > 1) {
            users = repositoryCollector.getUsers().findAllBySearchName(qSplit, true);
        } else {
            users = repositoryCollector.getUsers().FindAllBySearch(q, true);
        }

        return users.stream().map(PartialUserWithEmpIdDto::fromEntity
        ).collect(Collectors.toList());
    }
}
