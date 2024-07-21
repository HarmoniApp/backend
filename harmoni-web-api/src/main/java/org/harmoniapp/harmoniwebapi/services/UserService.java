package org.harmoniapp.harmoniwebapi.services;

import com.google.common.collect.Lists;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.UserDto;
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
    private final int page_size = 20;

    public List<UserDto> getUsers(int page) {
        List<User> users = repositoryCollector.getUsers().findAll();
        List<List<User>> pagedUsers = Lists.partition(users, page_size);

        return pagedUsers.get(page).stream()
                .map(UserDto::fromEntity)
                .toList();
    }

    public UserDto getUser(long id) {
        var userOptional = repositoryCollector.getUsers().findById(id);

        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException();
        }

        var user = userOptional.get();

        return UserDto.fromEntity(user);
    }

    public UserDto add(UserDto userDto) {
        User user = userDto.toEntity();

        var contractType = repositoryCollector.getContractTypes().findById(userDto.contractType().getId());
        if (contractType.isEmpty()) {
            throw new EntityNotFoundException();
        }
        user.setContractType(contractType.get());

        var supervisor = repositoryCollector.getUsers().findById(userDto.supervisorId());
        if (supervisor.isEmpty()) {
            user.setSupervisor(null);
        } else {
            user.setSupervisor(supervisor.get());
        }

        user.setResidence(userDto.residence().toEntity());
        user.setWorkAddress(userDto.workAddress().toEntity());

        user.setLanguages(
                userDto.languages().stream()
                        .map(p -> repositoryCollector.getLanguages().findById(p.getId()).get())
                        .collect(Collectors.toSet()));

        user.setRoles(
                userDto.roles().stream()
                        .map(p -> repositoryCollector.getRoles().findById(p.getId()).get())
                        .collect(Collectors.toSet())
        );

        var response = repositoryCollector.getUsers().save(user);
        return UserDto.fromEntity(response);
    }

    @Transactional
    public UserDto update(long id, UserDto userDto) {
        var existingUser = repositoryCollector.getUsers().findById(id);

        User user = userDto.toEntity();

        user.setId(
                existingUser.map(User::getId).orElse(null)
        );

        var contractType = repositoryCollector.getContractTypes().findById(userDto.contractType().getId());
        if (contractType.isEmpty()) {
            throw new EntityNotFoundException();
        }
        user.setContractType(contractType.get());

        var supervisor = repositoryCollector.getUsers().findById(userDto.supervisorId());
        if (supervisor.isEmpty()) {
            user.setSupervisor(null);
        } else {
            user.setSupervisor(supervisor.get());
        }

        user.setResidence(userDto.residence().toEntity());
        user.setWorkAddress(userDto.workAddress().toEntity());

        user.setLanguages(
                userDto.languages().stream()
                        .map(p -> repositoryCollector.getLanguages().findById(p.getId()).get())
                        .collect(Collectors.toSet()));

        user.setRoles(
                userDto.roles().stream()
                        .map(p -> repositoryCollector.getRoles().findById(p.getId()).get())
                        .collect(Collectors.toSet())
        );

        var response = repositoryCollector.getUsers().save(user);
        return UserDto.fromEntity(response);
    }

    public void delete(long id) {
        var userOptional = repositoryCollector.getUsers().findById(id);
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException();
        }
        repositoryCollector.getUsers().deleteById(id);
    }
}
