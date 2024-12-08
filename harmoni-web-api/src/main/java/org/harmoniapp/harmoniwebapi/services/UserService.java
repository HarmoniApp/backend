package org.harmoniapp.harmoniwebapi.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.Address;
import org.harmoniapp.harmonidata.entities.ContractType;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.PageDto;
import org.harmoniapp.harmoniwebapi.contracts.UserDto;
import org.harmoniapp.harmoniwebapi.contracts.UserNewPassword;
import org.harmoniapp.harmoniwebapi.exception.EasyPasswordException;
import org.harmoniapp.harmoniwebapi.utils.PasswordManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
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
    private final String photoDirPath = "harmoni-web-api/src/main/resources/static/userPhoto/";

    /**
     * Retrieves a paginated list of UserDto objects based on specified criteria.
     *
     * @param roles      List of role IDs to filter users by roles.
     * @param contracts  List of contract IDs to filter users by contracts.
     * @param languages  List of language IDs to filter users by languages.
     * @param pageNumber The page number to retrieve.
     * @param pageSize   The number of records per page.
     * @param sortBy     Field name by which the results should be sorted.
     * @param order      Sort order for the results. Can be "asc" for ascending or "desc" for descending. Defaults to "asc" if null or empty.
     * @return A PageDto containing a list of UserDto objects matching the specified criteria, sorted as requested.
     */
    public PageDto<UserDto> getUsers(List<Long> roles, List<Long> contracts, List<Long> languages, int pageNumber, int pageSize, String sortBy, String order) {
        pageNumber = (pageNumber < 1) ? 0 : pageNumber - 1;
        pageSize = (pageSize < 1) ? 10 : pageSize;

        Sort.Direction sortDirection;
        if (order == null || order.isEmpty() || order.equalsIgnoreCase("asc")) {
            sortDirection = Sort.Direction.ASC;
        } else {
            sortDirection = Sort.Direction.DESC;
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortDirection, sortBy));
        Page<User> users;
        if ((roles == null || roles.isEmpty())
                && (contracts == null || contracts.isEmpty())
                && (languages == null || languages.isEmpty())) {
            users = repositoryCollector.getUsers().findAllByIsActive(true, pageable);
        } else {
            users = repositoryCollector.getUsers().findAllByContractAndRoleAndLanguageAndIsActive(contracts, roles, languages, true, pageable);
        }
        return new PageDto<>(users.stream().map(UserDto::fromEntity).toList(),
                users.getSize(),
                users.getNumber() + 1,
                users.getTotalPages());
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
                .findByIdAndIsActive(id, true)
                .orElseThrow(IllegalArgumentException::new);

        return UserDto.fromEntity(user);
    }

    public int getUserAvailableAbsenceDays(long id) {
        User user = repositoryCollector.getUsers().findById(id)
                .orElseThrow(IllegalArgumentException::new);

        return user.getAvailableAbsenceDays() + user.getUnusedAbsenceDays();
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
        user.setIsActive(true);
        user.setPhoto("default.jpg");
        user.setAvailableAbsenceDays(contractType.getAbsenceDays());
        user.setPasswordExpirationDate(LocalDate.now().minusDays(1));

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
        List<String> defaultPhotos = List.of("default.jpg", "man.jpg", "woman.jpg");

        User user = repositoryCollector.getUsers().findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + id + " not found"));

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null ||
                !(originalFilename.endsWith(".jpg") || originalFilename.endsWith(".png") || originalFilename.endsWith(".jpeg"))) {
            throw new IllegalArgumentException("File must be a JPG or PNG image");
        }

        String uploadDirectory = new File(photoDirPath).getAbsolutePath();

        File directory = new File(uploadDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try {
            String oldPhoto = user.getPhoto();
            Path oldPhotoPath = Paths.get(uploadDirectory, oldPhoto);

            String newFileName = user.getId() + "_" + originalFilename;
            Path path = Paths.get(uploadDirectory, newFileName);

            Files.write(path, file.getBytes());

            user.setPhoto(newFileName);
            repositoryCollector.getUsers().save(user);

            if (!defaultPhotos.contains(oldPhoto) && Files.exists(oldPhotoPath)) {
                Files.delete(oldPhotoPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }

        return UserDto.fromEntity(user);
    }

    /**
     * Sets the user's photo to the default photo.
     *
     * @param id The ID of the user whose photo is to be set to default.
     * @return The updated UserDto object with the default photo.
     * @throws IllegalArgumentException if the user with the specified ID is not found.
     * @throws RuntimeException         if there is an error deleting the old photo file.
     */
    public UserDto setDefaultPhoto(long id) {
        User user = repositoryCollector.getUsers().findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + id + " not found"));

        String oldPhoto = user.getPhoto();
        if (oldPhoto.equals("default.jpg")) {
            return UserDto.fromEntity(user);
        }

        String uploadDirectory = new File(photoDirPath).getAbsolutePath();
        File directory = new File(uploadDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try {
            Path oldPhotoPath = Paths.get(uploadDirectory, oldPhoto);
            Files.delete(oldPhotoPath);

            user.setPhoto("default.jpg");
            repositoryCollector.getUsers().save(user);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }

        return UserDto.fromEntity(user);
    }

    /**
     * Retrieves the photo of a specific user by their ID.
     *
     * @param id The ID of the user whose photo is to be retrieved.
     * @return A ResponseEntity containing the InputStreamResource of the user's photo.
     * @throws IllegalArgumentException if the user with the specified ID is not found.
     * @throws RuntimeException if there is an error reading the photo file.
     */
    public ResponseEntity<InputStreamResource> getUserPhoto(long id) {
        User user = repositoryCollector.getUsers().findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + id + " not found"));

        String photo = user.getPhoto();
        MediaType contentType = photo.endsWith(".png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG;

        String uploadDirectory = new File(photoDirPath).getAbsolutePath();
        Path path = Paths.get(uploadDirectory, photo);
        if (!Files.exists(path)) {
            path = Paths.get(uploadDirectory, "default.jpg");
        }

        try {
            InputStream in = new FileInputStream(path.toString());
            return ResponseEntity.ok().contentType(contentType).body(new InputStreamResource(in));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read photo file", e);
        }
    }

    /**
     * Marks a user as inactive (soft delete) by their ID.
     *
     * @param id The ID of the user to deactivate.
     * @throws IllegalArgumentException if the user with the specified ID is not found.
     * @throws IllegalStateException    if the user is already deactivated.
     */
    public void delete(long id) {
        var user = repositoryCollector.getUsers().findById(id)
                .orElseThrow(IllegalArgumentException::new);

        if (!user.getIsActive()) {
            throw new IllegalStateException("User is already deactivated.");
        }

        user.setIsActive(false);
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
            users = repositoryCollector.getUsers().findAllBySearchName(qSplit, true);
        } else {
            users = repositoryCollector.getUsers().FindAllBySearch(q, true);
        }

        return users.stream().map(UserDto::fromEntity).collect(Collectors.toList());
    }

    /**
     * Changes the password for a user.
     *
     * @param id  The ID of the user whose password is to be changed.
     * @param pwd The new password to set for the user.
     * @return A message indicating the password change was successful.
     * @throws EasyPasswordException    If the provided password is compromised.
     * @throws IllegalArgumentException If the user with the specified ID is not found.
     */
    public String changePassword(long id, UserNewPassword pwd) {
        if (passwordChecker.check(pwd.newPassword()).isCompromised()) {
            throw new EasyPasswordException();
        }

        User user = repositoryCollector.getUsers().findById(id)
                .orElseThrow(IllegalArgumentException::new);

        String hashedPwd = passwordEncoder.encode(pwd.newPassword());
        user.setPassword(hashedPwd);
        user.setPasswordExpirationDate(LocalDate.now().plusMonths(6));
        user.setFailedLoginAttempts(0);

        repositoryCollector.getUsers().save(user);
        return "Password changed successfully";
    }

    /**
     * Generates a new password for the user with the specified ID.
     * <p>
     * This method generates a new common text password, hashes it, and sets it as the user's password.
     * The password expiration date is set to yesterday, and the failed login attempts are reset to 0.
     * </p>
     *
     * @param id The ID of the user for whom the new password is generated.
     * @return The newly generated password in plain text.
     * @throws IllegalArgumentException if the user with the specified ID is not found.
     */
    public String generateNewPassword(long id) {
        User user = repositoryCollector.getUsers().findById(id)
                .orElseThrow(IllegalArgumentException::new);

        String pwd = passwordManager.generateCommonTextPassword();
        String hashedPwd = passwordEncoder.encode(pwd);
        user.setPassword(hashedPwd);
        user.setPasswordExpirationDate(LocalDate.now().minusDays(1));
        user.setFailedLoginAttempts(0);

        repositoryCollector.getUsers().save(user);

        return pwd;
    }

    public void carryOverPreviousYearAbsenceDays(User user) {
        if (user.getAvailableAbsenceDays() > 0) {
            user.setUnusedAbsenceDays(user.getAvailableAbsenceDays());
            user.setUnusedAbsenceExpiration(LocalDate.of(LocalDate.now().getYear() + 1, 9, 30));
            user.setAvailableAbsenceDays(0);
        }

        int newAbsenceDays = repositoryCollector.getContractTypes()
                .findById(user.getContractType().getId())
                .orElseThrow(() -> new RuntimeException("Contract type not found"))
                .getAbsenceDays();

        user.setAvailableAbsenceDays(newAbsenceDays);
        repositoryCollector.getUsers().save(user);
    }

    public void expireUnusedAbsenceDays(User user) {
        if (user.getUnusedAbsenceExpiration() != null && user.getUnusedAbsenceExpiration().isBefore(LocalDate.now())) {
            user.setUnusedAbsenceDays(0);
            user.setUnusedAbsenceExpiration(null);
            repositoryCollector.getUsers().save(user);
        }
    }

    @Scheduled(cron = "0 0 0 1 1 ?")  // Every year 01.01 at 00:00
    public void scheduledCarryOverPreviousYearAbsenceDays() {
        List<User> users = repositoryCollector.getUsers().findAllByIsActive(true);
        for (User user : users) {
            carryOverPreviousYearAbsenceDays(user);
        }
    }

    @Scheduled(cron = "0 0 0 1 10 ?")  // Every year 01.10 at 00:00
    public void scheduledExpireUnusedAbsenceDays() {
        List<User> users = repositoryCollector.getUsers().findAllByIsActive(true);
        for (User user : users) {
            expireUnusedAbsenceDays(user);
        }
    }

}
