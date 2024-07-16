package org.harmoniapp.harmoniwebapi.services;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.Address;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.entities.UserLanguage;
import org.harmoniapp.harmonidata.entities.UserRole;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.AddressDto;
import org.harmoniapp.harmoniwebapi.contracts.UserDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

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
                .map(p -> new UserDto(
                        p.getId(),
                        p.getFirstname(),
                        p.getSurname(),
                        p.getEmail(),
                        p.getContractType(),
                        p.getContractSignature(),
                        p.getContractExpiration(),
                        mapAddress(p.getResidence()),
                        mapAddress(p.getWorkAddress()),
                        getSupervisor(p),
                        p.getPhoneNumber(),
                        p.getEmployeeId(),
                        p.getRoles().stream().map(UserRole::getRole).collect(Collectors.toSet()),
                        p.getLanguages().stream().map(UserLanguage::getLanguage).collect(Collectors.toSet())
                        )
                ).toList();
    }

    private AddressDto mapAddress(Address address) {
        return new AddressDto(
                address.getId(),
                address.getZipCode(),
                address.getCity(),
                address.getStreet(),
                address.getBuildingNumber(),
                address.getApartment()
        );
    }

    private long getSupervisor(User user) {
        long response = -1;
        if (user.getSupervisor() != null) {
            response = user.getSupervisor().getId();
        }
        return response;
    }
}
