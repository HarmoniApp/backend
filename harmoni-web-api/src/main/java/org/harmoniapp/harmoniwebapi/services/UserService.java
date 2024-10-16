package org.harmoniapp.harmoniwebapi.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.Address;
import org.harmoniapp.harmonidata.entities.ContractType;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.UserDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
                .filter(User::isActive)
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
                .filter(User::isActive)
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
        if (userDto.contractExpiration().isBefore(userDto.contractSignature())) {
            throw new IllegalArgumentException("Contract expiration date must be after the contract signature date.");
        }

        User user = userDto.toEntity();

        ContractType contractType = repositoryCollector.getContractTypes()
                .findById(userDto.contractType().getId())
                .orElseThrow(IllegalArgumentException::new);

        user.setContractType(contractType);

        User supervisor = repositoryCollector.getUsers().findById(userDto.supervisorId()).orElse(null);
        user.setSupervisor(supervisor);

        Address residence = addressService.saveAddressEntity(userDto.residence());
        user.setResidence(residence);

        Address workAddress = repositoryCollector.getAddresses().findById(userDto.workAddress().toEntity().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Department with ID " + userDto.workAddress().id() + " not found"));

        user.setWorkAddress(workAddress);
        user.setLastPasswordChange(LocalDateTime.now());
        user.setPasswordGenerated(true);
        user.setActive(true);
        user.setPhoto("default.jpg");

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
        if (userDto.contractExpiration().isBefore(userDto.contractSignature())) {
            throw new IllegalArgumentException("Contract expiration date must be after the contract signature date.");
        }

        var existingUser = repositoryCollector.getUsers().findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + id + " not found"));

        ContractType contractType = repositoryCollector.getContractTypes()
                .findById(userDto.contractType().getId())
                .orElseThrow(() -> new IllegalArgumentException("Contract Type with ID " + id + " not found"));

        User supervisor = repositoryCollector.getUsers().findById(userDto.supervisorId())
                .orElse(null);

        existingUser.setFirstname(userDto.firstname());
        existingUser.setSurname(userDto.surname());
        existingUser.setEmail(userDto.email());
        existingUser.setContractType(contractType);
        existingUser.setSupervisor(supervisor);
        existingUser.setContractSignature(userDto.contractSignature());
        existingUser.setContractExpiration(userDto.contractExpiration());
        existingUser.setPhoneNumber(userDto.phoneNumber());
        existingUser.setEmployeeId(userDto.employeeId());

        Address residence = existingUser.getResidence();
        residence = addressService.updateAddress(residence, userDto.residence());
        existingUser.setResidence(residence);

        Long newDepartmentId = userDto.workAddress().id();
        Address newDepartment = repositoryCollector.getAddresses().findById(newDepartmentId)
                .orElseThrow(() -> new IllegalArgumentException("Department with ID " + id + " not found"));
        existingUser.setWorkAddress(newDepartment);

        existingUser.setLanguages(
                userDto.languages().stream()
                        .map(p -> repositoryCollector.getLanguages().findById(p.id()).get())
                        .collect(Collectors.toSet()));

        existingUser.setRoles(
                userDto.roles().stream()
                        .map(p -> repositoryCollector.getRoles().findById(p.getId()).get())
                        .collect(Collectors.toSet())
        );

        User response = repositoryCollector.getUsers().save(existingUser);
        return UserDto.fromEntity(response);
    }

    /**
     * Uploads a photo for a specific user and saves it to the disk.
     *
     * @param id   The ID of the user to associate the photo with.
     * @param file The photo file to be uploaded (must be in JPG or PNG format).
     * @return The updated UserDto object after saving the photo information.
     * @throws IllegalArgumentException if the user is not found or the file format is not supported.
     */
    public UserDto uploadPhoto(long id, MultipartFile file) {
        User user = repositoryCollector.getUsers().findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + id + " not found"));

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !(originalFilename.endsWith(".jpg") || originalFilename.endsWith(".png"))) {
            throw new IllegalArgumentException("File must be a JPG or PNG image");
        }

        String uploadDirectory = new File("./harmoni-web-api/src/main/resources/static/userPhoto/").getAbsolutePath();

        File directory = new File(uploadDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try {
            String newFileName = user.getId() + "_" + originalFilename;
            Path path = Paths.get(uploadDirectory, newFileName);

            Files.write(path, file.getBytes());

            user.setPhoto(newFileName);
            repositoryCollector.getUsers().save(user);

        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }

        return UserDto.fromEntity(user);
    }

    /**
     * Marks a user as inactive (soft delete) by their ID.
     *
     * @param id The ID of the user to deactivate.
     * @throws IllegalArgumentException if the user with the specified ID is not found.
     * @throws IllegalStateException if the user is already deactivated.
     */
    public void delete(long id) {
        var user = repositoryCollector.getUsers().findById(id)
                .orElseThrow(IllegalArgumentException::new);

        if (!user.isActive()) {
            throw new IllegalStateException("User is already deactivated.");
        }

        user.setActive(false);
        repositoryCollector.getUsers().save(user);
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
}
