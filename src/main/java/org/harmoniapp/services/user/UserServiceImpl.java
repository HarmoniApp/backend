package org.harmoniapp.services.user;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.PageDto;
import org.harmoniapp.contracts.PageRequestDto;
import org.harmoniapp.contracts.user.UserDto;
import org.harmoniapp.contracts.user.UserSearchParamsDto;
import org.harmoniapp.entities.profile.Address;
import org.harmoniapp.entities.profile.ContractType;
import org.harmoniapp.entities.profile.Language;
import org.harmoniapp.entities.profile.Role;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.exception.EntityNotFound;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.services.profile.AddressService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the UserService interface.
 * This service provides methods for managing and retrieving user data.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final RepositoryCollector repositoryCollector;
    private final UserPasswordServiceImpl userPassword;
    private final AddressService addressService;
    private final UserSearchService userSearchService;
    private final FindUser findUser;

    /**
     * Retrieves a specific user by their ID.
     *
     * @param id The ID of the user to retrieve.
     * @return The UserDto object corresponding to the specified ID.
     * @throws EntityNotFound if the user with the specified ID is not found.
     */
    public UserDto get(long id) {
        User user = findUser.getUserById(id, repositoryCollector);
        return UserDto.fromEntity(user);
    }

    /**
     * Retrieves a paginated list of users based on search parameters and pagination details.
     *
     * @param searchParamsDto The search parameters for filtering users.
     * @param pageRequestDto  The pagination and sorting details.
     * @return A PageDto containing the list of UserDto objects, page size, current page number, and total pages.
     */
    public PageDto<UserDto> getPage(UserSearchParamsDto searchParamsDto, PageRequestDto pageRequestDto) {
        Page<User> users = userSearchService.findUsersPage(pageRequestDto, searchParamsDto);
        return PageDto.mapPage(users, UserDto::fromEntity);
    }

    /**
     * Searches for users based on a query string.
     *
     * @param q The query string used to search for users. Must not be null or empty.
     * @return A list of UserDto objects that match the search criteria.
     * @throws IllegalArgumentException if the query string is null or empty.
     */
    public List<UserDto> getUsersSearch(String q) {
        return userSearchService.searchUsers(q)
                .stream()
                .map(UserDto::fromEntity)
                .toList();
    }

    /**
     * Adds a new user.
     *
     * @param userDto The UserDto object representing the new user.
     * @return The created UserDto object.
     */
    @Transactional
    public UserDto create(UserDto userDto) {
        validateContractDates(userDto);

        User user = userDto.toEntity();
        populateUserDetails(user, userDto);
        String rawPass = userPassword.setPassword(user);

        User response = repositoryCollector.getUsers().save(user);
        return UserDto.fromEntity(response, rawPass);
    }

    /**
     * Marks a user as inactive (soft delete) by their ID.
     *
     * @param id The ID of the user to deactivate.
     * @throws IllegalArgumentException if the user with the specified ID is not found.
     * @throws IllegalStateException    if the user is already deactivated.
     */
    @Transactional
    public void delete(long id) {
        var user = repositoryCollector.getUsers().findByIdAndIsActive(id, true)
                .orElseThrow(() -> new EntityNotFound("Nie znaleziono użytkownika o ID %d".formatted(id)));

        user.setIsActive(false);
        repositoryCollector.getUsers().save(user);
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
        validateContractDates(userDto);

        User existingUser = findUser.getUserById(id, repositoryCollector);
        updateUserDetails(existingUser, userDto);
        updateUserAddresses(existingUser, userDto);
        setContractType(existingUser, userDto);
        setSupervisor(existingUser, userDto);
        setLanguages(existingUser, userDto);
        setRoles(existingUser, userDto);

        User response = repositoryCollector.getUsers().save(existingUser);
        return UserDto.fromEntity(response);
    }

    /**
     * Validates the contract dates of the given UserDto.
     * Ensures that the contract expiration date is after the contract signature date.
     *
     * @param userDto The UserDto object containing the contract dates to validate.
     * @throws IllegalArgumentException if the contract expiration date is before the contract signature date.
     */
    private void validateContractDates(UserDto userDto) {
        if (userDto.contractExpiration().isBefore(userDto.contractSignature())) {
            throw new IllegalArgumentException("Contract expiration date must be after the contract signature date.");
        }
    }

    /**
     * Sets the contract type for the given user based on the provided UserDto.
     *
     * @param user    The User entity to set the contract type for.
     * @param userDto The UserDto containing the contract type information.
     * @throws IllegalArgumentException if the contract type is not found.
     */
    private void setContractType(User user, UserDto userDto) {
        ContractType contractType = repositoryCollector.getContractTypes()
                .findById(userDto.contractType().getId())
                .orElseThrow(IllegalArgumentException::new);
        user.setContractType(contractType);
    }

    /**
     * Sets the supervisor for the given user based on the provided UserDto.
     *
     * @param user    The User entity to set the supervisor for.
     * @param userDto The UserDto containing the supervisor ID.
     */
    private void setSupervisor(User user, UserDto userDto) {
        User supervisor = repositoryCollector.getUsers().findById(userDto.supervisorId()).orElse(null);
        user.setSupervisor(supervisor);
    }

    /**
     * Sets the addresses for the given user based on the provided UserDto.
     *
     * @param user    The User entity to set the addresses for.
     * @param userDto The UserDto containing the address information.
     */
    private void setAddresses(User user, UserDto userDto) {
        Address residence = addressService.saveAddressEntity(userDto.residence());
        user.setResidence(residence);

        Address workAddress = getDepartment(userDto.workAddress().id());
        user.setWorkAddress(workAddress);
    }

    /**
     * Sets the default details for a user.
     * Ensures that the user has a contract type, sets the user as active,
     * assigns a default photo, and initializes absence days.
     *
     * @param user The User entity to set the details for.
     * @throws IllegalArgumentException if the user does not have a contract type.
     */
    private void setUserDetails(User user) {
        if (user.getContractType() == null) {
            throw new IllegalArgumentException("Contract type is required");
        }
        user.setIsActive(true);
        user.setPhoto("default.jpg");
        user.setAvailableAbsenceDays(user.getContractType().getAbsenceDays());
        user.setUnusedAbsenceDays(0);

        LocalDate now = LocalDate.now();
        user.setUnusedAbsenceExpiration(LocalDate.of(now.getYear(), 9, 30)); //TODO: Czy jest to potrzebne?
    }

    /**
     * Sets the languages for the given user based on the provided UserDto.
     *
     * @param user    The User entity to set the languages for.
     * @param userDto The UserDto containing the language information.
     * @throws IllegalArgumentException if a language with the specified ID is not found.
     */
    private void setLanguages(User user, UserDto userDto) {
        List<Language> languages = repositoryCollector.getLanguages().findAll();

        user.setLanguages(
                userDto.languages().stream()
                        .map(p -> languages.stream()
                                .filter(l -> l.getId().equals(p.id()))
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException("Language with ID " + p.id() + " not found"))
                        )
                        .collect(Collectors.toSet()));
    }

    /**
     * Sets the roles for the given user based on the provided UserDto.
     *
     * @param user    The User entity to set the roles for.
     * @param userDto The UserDto containing the role information.
     * @throws IllegalArgumentException if a role with the specified ID is not found.
     */
    private void setRoles(User user, UserDto userDto) {
        List<Role> roles = repositoryCollector.getRoles().findAll();

        user.setRoles(
                userDto.roles().stream()
                        .map(p ->
                                roles.stream()
                                        .filter(r -> r.getId().equals(p.id()))
                                        .findFirst()
                                        .orElseThrow(() -> new IllegalArgumentException("Role with ID " + p.id() + " not found"))
                        )
                        .collect(Collectors.toSet()));
    }

    /**
     * Populates the user details by setting various attributes such as contract type,
     * supervisor, addresses, languages, roles, and other default details.
     *
     * @param user    The User entity to populate details for.
     * @param userDto The UserDto containing the information to populate the user details.
     */
    private void populateUserDetails(User user, UserDto userDto) {
        setContractType(user, userDto);
        setSupervisor(user, userDto);
        setAddresses(user, userDto);
        setLanguages(user, userDto);
        setRoles(user, userDto);
        setUserDetails(user);
    }

    /**
     * Updates the details of an existing user with the information provided in the UserDto.
     *
     * @param existingUser The User entity to update.
     * @param userDto      The UserDto containing the updated user data.
     */
    private void updateUserDetails(User existingUser, UserDto userDto) {
        existingUser.setFirstname(userDto.firstname());
        existingUser.setSurname(userDto.surname());
        existingUser.setEmail(userDto.email());
        existingUser.setContractSignature(userDto.contractSignature());
        existingUser.setContractExpiration(userDto.contractExpiration());
        existingUser.setPhoneNumber(userDto.phoneNumber());
        existingUser.setEmployeeId(userDto.employeeId());
    }

    /**
     * Updates the addresses of an existing user.
     *
     * @param existingUser The User entity to update.
     * @param userDto      The UserDto containing the updated address information.
     */
    private void updateUserAddresses(User existingUser, UserDto userDto) {
        Address residence = addressService.updateAddress(existingUser.getResidence(), userDto.residence());
        existingUser.setResidence(residence);

        Address newDepartment = getDepartment(userDto.workAddress().id());
        existingUser.setWorkAddress(newDepartment);
    }

    /**
     * Retrieves the department address by its ID.
     *
     * @param departmentId The ID of the department to retrieve.
     * @return The Address entity corresponding to the specified department ID.
     * @throws EntityNotFound if the department with the specified ID is not found.
     */
    private Address getDepartment(Long departmentId) {
        return repositoryCollector.getAddresses().findById(departmentId)
                .orElseThrow(() -> new EntityNotFound("Nie znaleziono oddziału o ID %d".formatted(departmentId)));
    }
}