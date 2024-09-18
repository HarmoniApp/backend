package org.harmoniapp.harmoniwebapi.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.Address;
import org.harmoniapp.harmonidata.entities.ContractType;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.UserDto;
import org.harmoniapp.harmoniwebapi.exception.EasyPasswordException;
import org.harmoniapp.harmoniwebapi.utils.PasswordManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final PasswordManager passwordManager;
    private final PasswordEncoder passwordEncoder;
    private final CompromisedPasswordChecker passwordChecker;


    /**
     * Retrieves a list of UserDto objects based on specified criteria.
     *
     * @param roles     List of role IDs to filter users by roles.
     * @param contracts List of contract IDs to filter users by contracts.
     * @param languages List of language IDs to filter users by languages.
     * @param sortBy    Field name by which the results should be sorted.
     * @param order     Sort order for the results. Can be "asc" for ascending or "desc" for descending. Defaults to "asc" if null or empty.
     * @return A list of UserDto objects matching the specified criteria, sorted as requested.
     */
    public List<UserDto> getUsers(List<Long> roles, List<Long> contracts, List<Long> languages, String sortBy, String order) {
        Sort.Direction sortDirection;
        if (order == null || order.isEmpty() || order.equalsIgnoreCase("asc")) {
            sortDirection = Sort.Direction.ASC;
        } else {
            sortDirection = Sort.Direction.DESC;
        }
        Sort sort = Sort.by(sortDirection, sortBy);

        List<User> users;
        if ((roles == null || roles.isEmpty())
                && (contracts == null || contracts.isEmpty())
                && (languages == null || languages.isEmpty())) {
            users = repositoryCollector.getUsers().findAll(sort);
        } else {
            users = repositoryCollector.getUsers().findAllByContractAndRoleAndLanguage(contracts, roles, languages, sort);
        }
        return users.stream()
                .map(UserDto::fromEntity)
                .toList();
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

        String rawPwd = passwordManager.generateCommonTextPassword();
        String hashedPwd = passwordEncoder.encode(rawPwd);
        user.setPassword(hashedPwd);

        User response = repositoryCollector.getUsers().save(user);
        return UserDto.fromEntityWithPassword(response, rawPwd);
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

    /**
     * Searches for users based on a query string.
     *
     * @param q The query string used to search for users. Must not be null or empty.
     * @return A list of UserDto objects that match the search criteria.
     * @throws IllegalArgumentException if the query string is null or empty.
     */
    public List<UserDto> getUsersSearch(String q) {
        if (q == null || q.isEmpty()) {
            throw new IllegalArgumentException();
        }

        q = q.toUpperCase();
        List<String> qSplit = List.of(q.split(" "));

        List<User> users;
        if (qSplit.size() > 1) {
            users = repositoryCollector.getUsers().findAllBySearchName(qSplit);
        } else {
            users = repositoryCollector.getUsers().FindAllBySearch(q);
        }

        return users.stream().map(UserDto::fromEntity).collect(Collectors.toList());
    }

    public String changePassword(long id, String pwd) {
        if (passwordChecker.check(pwd).isCompromised()) {
            throw new EasyPasswordException();
        }

        User user = repositoryCollector.getUsers().findById(id)
                .orElseThrow(IllegalArgumentException::new);

        String hashedPwd = passwordEncoder.encode(pwd);
        user.setPassword(hashedPwd);

        //TODO: set field (like) needChanged to FALSE and date of lats password change

        repositoryCollector.getUsers().save(user);

        return "Password changed successfully";
    }

    public String generateNewPassword(long id) {
        User user = repositoryCollector.getUsers().findById(id)
                .orElseThrow(IllegalArgumentException::new);

        String pwd = passwordManager.generateCommonTextPassword();
        String hashedPwd = passwordEncoder.encode(pwd);
        user.setPassword(hashedPwd);

        //TODO: set field (like) needChanged to TRUE and date of lats password change

        repositoryCollector.getUsers().save(user);

        return pwd;
    }
}
