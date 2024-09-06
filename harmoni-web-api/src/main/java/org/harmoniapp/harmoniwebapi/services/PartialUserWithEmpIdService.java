package org.harmoniapp.harmoniwebapi.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.PartialUserWithEmpIdDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.List;

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
     * Retrieves all users and maps their data to PartialUserWithEmpIdDto.
     *
     * @return a list of PartialUserWithEmpIdDto objects representing partial user data, including their employee ID
     */
    public List<PartialUserWithEmpIdDto> getAllPartialUsers() {
        List<User> users = repositoryCollector.getUsers().findAll();

        return users.stream()
                .map(PartialUserWithEmpIdDto::fromEntity)
                .toList();
    }
}
