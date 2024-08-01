package org.harmoniapp.harmoniwebapi.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.Address;
import org.harmoniapp.harmonidata.entities.ContractType;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.UserDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing users.
 * Provides methods to perform CRUD operations on users.
 */
@Service
@RequiredArgsConstructor
@ComponentScan(basePackages = {"org.harmoniapp.harmonidata"})
public class UserService {
    private final RepositoryCollector repositoryCollector;
    private final AddressService addressService;
//    private final int page_size = 20;

    /**
     * Retrieves users with pagination support.
     *
     * @param page The page number to retrieve.
     * @return A list of UserDto objects for the specified page.
     */
    public List<UserDto> getUsers(int page) {
        List<User> users = repositoryCollector.getUsers().findAll();
        return users.stream()
                .map(UserDto::fromEntity)
                .toList();
        //        List<List<User>> pagedUsers = Lists.partition(users, page_size);

//        return pagedUsers.get(page).stream()
//                .map(UserDto::fromEntity)
//                .toList();
    }

    /**
     * Retrieves a specific user by their ID.
     *
     * @param id The ID of the user to retrieve.
     * @return The UserDto object corresponding to the specified ID.
     * @throws IllegalArgumentException if the user with the specified ID is not found.
     */
    public UserDto getUser(long id) {
        User user = repositoryCollector.getUsers()
                .findById(id)
                .orElseThrow(IllegalArgumentException::new);

        return UserDto.fromEntity(user);
    }

    /**
     * Adds a new user.
     *
     * @param userDto The UserDto object representing the new user.
     * @return The created UserDto object.
     */
    @Transactional
    public UserDto add(UserDto userDto) {
        User user = userDto.toEntity();

        ContractType contractType = repositoryCollector.getContractTypes()
                .findById(userDto.contractType().getId())
                .orElseThrow(IllegalArgumentException::new);

        user.setContractType(contractType);

        User supervisor = repositoryCollector.getUsers().findById(userDto.supervisorId()).orElse(null);
        user.setSupervisor(supervisor);

        Address residence = addressService.saveAddressEntity(userDto.residence());
        user.setResidence(residence);

        Address workAddress = addressService.saveAddressEntity(userDto.workAddress());
        user.setWorkAddress(workAddress);

        user.setLanguages(
                userDto.languages().stream()
                        .map(p -> repositoryCollector.getLanguages().findById(p.id()).get())
                        .collect(Collectors.toSet()));

        user.setRoles(
                userDto.roles().stream()
                        .map(p -> repositoryCollector.getRoles().findById(p.getId()).get())
                        .collect(Collectors.toSet())
        );

        User response = repositoryCollector.getUsers().save(user);
        return UserDto.fromEntity(response);
    }

    /**
     * Updates an existing user.
     *
     * @param id      The ID of the user to update.
     * @param userDto The UserDto object containing the updated user data.
     * @return The updated UserDto object.
     */
    @Transactional
    public UserDto update(long id, UserDto userDto) {
        var existingUser = repositoryCollector.getUsers().findById(id);

        User user = userDto.toEntity();

        user.setId(
                existingUser.map(User::getId).orElse(null)
        );

        ContractType contractType = repositoryCollector.getContractTypes()
                .findById(userDto.contractType().getId())
                .orElseThrow(IllegalArgumentException::new);

        user.setContractType(contractType);

        User supervisor = repositoryCollector.getUsers().findById(userDto.supervisorId()).orElse(null);
        user.setSupervisor(supervisor);

        Address residence;
        if (existingUser.isPresent()) {
            residence = existingUser.get().getResidence();
            residence = addressService.updateAddress(residence, userDto.residence());
        } else {
            residence = addressService.saveAddressEntity(userDto.residence());
        }
        user.setResidence(residence);

        Address workAddress;
        if (existingUser.isPresent()) {
            workAddress = existingUser.get().getWorkAddress();
            workAddress = addressService.updateAddress(workAddress, userDto.workAddress());
        } else {
            workAddress = addressService.saveAddressEntity(userDto.workAddress());
        }
        user.setWorkAddress(workAddress);

        user.setLanguages(
                userDto.languages().stream()
                        .map(p -> repositoryCollector.getLanguages().findById(p.id()).get())
                        .collect(Collectors.toSet()));

        user.setRoles(
                userDto.roles().stream()
                        .map(p -> repositoryCollector.getRoles().findById(p.getId()).get())
                        .collect(Collectors.toSet())
        );

        User response = repositoryCollector.getUsers().save(user);
        return UserDto.fromEntity(response);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id The ID of the user to delete.
     * @throws IllegalArgumentException if the user with the specified ID is not found.
     */
    public void delete(long id) {
        var userOptional = repositoryCollector.getUsers().findById(id);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException();
        }
        repositoryCollector.getUsers().deleteById(id);
    }

    public List<UserDto> getUsersWithFilter(List<String> roles, List<String> contracts, List<String> languages) {
        if (roles != null) {
            if (contracts != null) {
                if (languages != null) {
                    return getUsersByContractAndRoleAndLanguage(roles, contracts, languages);
                } else {
                    return getUsersByContractAndRole(contracts, roles);
                }
            } else {
                return getUsersByRole(roles);
            }
        } else if (contracts != null) {
            if (languages != null) {
                return getUsersByContractAndLanguage(contracts, languages);
            } else {
                return getUsersByContract(contracts);
            }
        } else if (languages != null) {
            return getUsersByLanguages(languages);
        }
        return getUsers(0);
    }

    private List<UserDto> getUsersByLanguages(List<String> languages) {
        List<Long> languageIds = new ArrayList<>();
        for (String lang : languages) {
            try {
                Long id = Long.valueOf(lang);
                languageIds.add(id);
            } catch (NumberFormatException ignored) {
            }
        }

        List<User> users = repositoryCollector.getUsers().findAllByContractAndRoleAndLanguage(null, null, null, null, languageIds, languages);
        return users.stream().map(UserDto::fromEntity).collect(Collectors.toList());
    }

    private List<UserDto> getUsersByContractAndLanguage(List<String> contracts, List<String> languages) {
        List<Long> contractIds = new ArrayList<>();
        for (String c : contracts) {
            try {
                Long id = Long.valueOf(c);
                contractIds.add(id);
            } catch (NumberFormatException ignored) {
            }
        }

        List<Long> languageIds = new ArrayList<>();
        for (String lang : languages) {
            try {
                Long id = Long.valueOf(lang);
                languageIds.add(id);
            } catch (NumberFormatException ignored) {
            }
        }

        List<User> users = repositoryCollector.getUsers().findAllByContractAndRoleAndLanguage(contractIds, contracts, null, null, languageIds, languages);
        return users.stream().map(UserDto::fromEntity).collect(Collectors.toList());
    }

    private List<UserDto> getUsersByContractAndRoleAndLanguage(List<String> roles, List<String> contracts, List<String> languages) {
        List<Long> roleIds = new ArrayList<>();
        for (String r : roles) {
            try {
                Long id = Long.valueOf(r);
                roleIds.add(id);
            } catch (NumberFormatException ignored) {
            }
        }

        List<Long> contractIds = new ArrayList<>();
        for (String c : contracts) {
            try {
                Long id = Long.valueOf(c);
                contractIds.add(id);
            } catch (NumberFormatException ignored) {
            }
        }

        List<Long> languageIds = new ArrayList<>();
        for (String lang : languages) {
            try {
                Long id = Long.valueOf(lang);
                languageIds.add(id);
            } catch (NumberFormatException ignored) {
            }
        }

        List<User> users = repositoryCollector.getUsers().findAllByContractAndRoleAndLanguage(contractIds, contracts, roleIds, roles, languageIds, languages);
        return users.stream().map(UserDto::fromEntity).collect(Collectors.toList());
    }

    private List<UserDto> getUsersByContract(List<String> contracts) {
        List<Long> contractIds = new ArrayList<>();
        for (String c : contracts) {
            try {
                Long id = Long.valueOf(c);
                contractIds.add(id);
            } catch (NumberFormatException ignored) {
            }
        }
        List<User> users = repositoryCollector.getUsers().findAllByContractAndRoleAndLanguage(contractIds, contracts, null, null, null, null);
        return users.stream().map(UserDto::fromEntity).collect(Collectors.toList());
    }

    private List<UserDto> getUsersByContractAndRole(List<String> contracts, List<String> roles) {
        List<Long> contractIds = new ArrayList<>();
        for (String c : contracts) {
            try {
                Long id = Long.valueOf(c);
                contractIds.add(id);
            } catch (NumberFormatException ignored) {
            }
        }

        List<Long> roleIds = new ArrayList<>();
        for (String r : roles) {
            try {
                Long id = Long.valueOf(r);
                roleIds.add(id);
            } catch (NumberFormatException ignored) {
            }
        }

        List<User> users = repositoryCollector.getUsers().findAllByContractAndRoleAndLanguage(contractIds, contracts, roleIds, roles, null, null);
        return users.stream().map(UserDto::fromEntity).collect(Collectors.toList());
    }

    private List<UserDto> getUsersByRole(List<String> roles) {
        List<Long> roleIds = new ArrayList<>();
        for (String r : roles) {
            try {
                Long id = Long.valueOf(r);
                roleIds.add(id);
            } catch (NumberFormatException ignored) {
            }
        }
        List<User> users = repositoryCollector.getUsers().findAllByContractAndRoleAndLanguage(null, null, roleIds, roles, null, null);
        return users.stream().map(UserDto::fromEntity).collect(Collectors.toList());
    }
}
