package org.harmoniapp.harmoniwebapi.services;

import com.google.common.collect.Lists;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.UserDto;
import org.harmoniapp.harmoniwebapi.mappers.MapEntityDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@ComponentScan(basePackages = {"org.harmoniapp.harmonidata"})
public class UserService {
    private final RepositoryCollector repositoryCollector;
    private final MapEntityDto<User, UserDto> mapper;
    private final int page_size = 20;

    public List<UserDto> getUsers(int page) {
        List<User> users = repositoryCollector.getUsers().findAll();
        List<List<User>> pagedUsers = Lists.partition(users, page_size);

        return pagedUsers.get(page).stream()
                .map(mapper::toDto)
                .toList();
    }

    public UserDto getUser(long id) {
        var userOptional = repositoryCollector.getUsers().findById(id);

        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException();
        }

        var user = userOptional.get();

        return mapper.toDto(user);
    }
}
