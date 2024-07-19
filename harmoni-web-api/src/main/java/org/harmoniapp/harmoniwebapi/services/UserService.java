package org.harmoniapp.harmoniwebapi.services;

import com.google.common.collect.Lists;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.entities.UserLanguage;
import org.harmoniapp.harmonidata.entities.UserRole;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.UserDto;
import org.harmoniapp.harmoniwebapi.mappers.MapEntityDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    public void add(UserDto userDto) {
        //TODO
    }

    @Transactional
    public long update(long id, UserDto userDto) {
        //TODO
        var existingUser = repositoryCollector.getUsers().findById(id);

        User user = existingUser.orElseGet(() -> mapper.toEntity(userDto));

        var supervisor = repositoryCollector.getUsers().findById(userDto.supervisorId());
        if (supervisor.isEmpty()) {
            user.setSupervisor(null);
        } else {
            user.setSupervisor(supervisor.get());
        }

        System.out.println(userDto.contractType());
        user.setLanguages(
                userDto.languages().stream()
                        .map(p -> new UserLanguage(user, p))
                        .collect(Collectors.toSet()));

        user.setRoles(
                userDto.roles().stream()
                        .map(p -> new UserRole(user, p))
                        .collect(Collectors.toSet())
        );

        var x = repositoryCollector.getUsers().saveAndFlush(user);
        return userDto.id();
    }

    public void delete(long id) {
        var userOptional = repositoryCollector.getUsers().findById(id);
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException();
        }
        repositoryCollector.getUsers().deleteById(id);
    }
}
