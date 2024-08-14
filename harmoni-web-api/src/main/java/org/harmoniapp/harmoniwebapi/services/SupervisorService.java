package org.harmoniapp.harmoniwebapi.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.SupervisorDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@ComponentScan(basePackages = {"org.harmoniapp.harmonidata"})
public class SupervisorService {
    private final RepositoryCollector repositories;

    public List<SupervisorDto> getAllSupervisors(){
        List<Long> roleIds = List.of(1L, 2L, 3L, 4L);
        List<User> users = repositories.getUsers().findSupervisors(roleIds);
        return users.stream()
                .map(SupervisorDto::fromEntity)
                .toList();
    }
}