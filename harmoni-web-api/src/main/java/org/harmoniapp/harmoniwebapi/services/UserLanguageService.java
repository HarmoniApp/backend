package org.harmoniapp.harmoniwebapi.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.entities.UserLanguage;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.UserDto;
import org.harmoniapp.harmoniwebapi.contracts.UserLanguageDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@ComponentScan(basePackages = {"org.harmoniapp.harmonidata"})
public class UserLanguageService {
    private final RepositoryCollector repositoryCollector;

    public UserLanguageDto getUser(long id) {
        var userOptional = repositoryCollector.getUsers().findById(id);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException();
        }

        var user = userOptional.get();

        return new UserLanguageDto(user.getId(), user.getFirstname(), user.getSurname(),
                user.getLanguages().stream().map(UserLanguage::getLanguage).collect(Collectors.toSet()));
    }

    public List<UserLanguageDto> getUsers() {
        List<User> users = repositoryCollector.getUsers().findAll();

        return users.stream()
                .map(p -> new UserLanguageDto(
                        p.getId(),
                        p.getFirstname(),
                        p.getSurname(),
                        p.getLanguages().stream().map(UserLanguage::getLanguage).collect(Collectors.toSet())
                )).toList();
    }
}
