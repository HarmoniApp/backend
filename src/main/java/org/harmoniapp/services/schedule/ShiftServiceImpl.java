package org.harmoniapp.services.schedule;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.notification.NotificationDto;
import org.harmoniapp.contracts.schedule.ShiftDto;
import org.harmoniapp.entities.profile.Role;
import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.exception.EntityNotFound;
import org.harmoniapp.exception.InvalidDateException;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.services.notification.NotificationService;
import org.harmoniapp.utils.ShiftNotificationManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Implementation of the ShiftService interface.
 * This service provides methods to manage shifts.
 */
@Service
@RequiredArgsConstructor
public class ShiftServiceImpl implements ShiftService {
    private final RepositoryCollector repositoryCollector;
    private final NotificationService notificationService;

    /**
     * Retrieves a ShiftDto for the shift with the specified ID.
     *
     * @param id the ID of the shift to retrieve
     * @return a ShiftDto containing the details of the shift
     * @throws EntityNotFound if the shift with the specified ID does not exist
     */
    public ShiftDto get(long id) throws EntityNotFound {
        Shift shift = repositoryCollector.getShifts().findById(id)
                .orElseThrow(() -> new EntityNotFound("Nie znaleziono zmiany o id: " + id));

        return ShiftDto.fromEntity(shift);
    }

    /**
     * Retrieves a list of ShiftDto for the shifts within the specified date range and user ID.
     *
     * @param startStr the start date and time of the range as a string
     * @param endStr   the end date and time of the range as a string
     * @param userId   the ID of the user whose shifts are to be retrieved
     * @return a list of ShiftDto containing the details of shifts within the date range for the specified user
     * @throws EntityNotFound       if the user with the specified ID does not exist
     * @throws InvalidDateException if the start date is after the end date
     */
    public List<ShiftDto> getShiftsByDateRangeAndUserId(String startStr, String endStr, Long userId) {
        validateUserId(userId);
        LocalDateTime start = parseDateTime(startStr);
        LocalDateTime end = parseDateTime(endStr);
        validateDateRange(start, end);
        List<Shift> shifts = repositoryCollector.getShifts().findAllByDateRangeAndUserId(start, end, userId);
        return shifts.stream()
                .map(ShiftDto::fromEntity)
                .toList();
    }

    /**
     * Validates the user ID.
     *
     * @param userId the ID of the user to validate
     * @throws EntityNotFound if the user ID is null or does not exist
     */
    private void validateUserId(Long userId) {
        if (userId == null || !repositoryCollector.getUsers().existsById(userId)) {
            throw new EntityNotFound("Nie znaleziono użytkownika o id: " + userId);
        }
    }

    /**
     * Parses a date-time string into a LocalDateTime object.
     *
     * @param dateTime the date-time string to parse
     * @return the parsed LocalDateTime object
     * @throws InvalidDateException if the date-time string is in an invalid format
     */
    private LocalDateTime parseDateTime(String dateTime) {
        try {
            return LocalDateTime.parse(dateTime);
        } catch (DateTimeParseException e) {
            throw new InvalidDateException("Nieprawidłowy format daty");
        }
    }

    /**
     * Validates the date range.
     *
     * @param start the start date and time of the range
     * @param end   the end date and time of the range
     * @throws InvalidDateException if the start date is after the end date
     */
    private void validateDateRange(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new InvalidDateException("Data początkowa nie może być po dacie końcowej");
        }
    }

    /**
     * Creates a new Shift.
     *
     * @param shiftDto the ShiftDto containing the details of the shift to create
     * @return the created ShiftDto
     * @throws EntityNotFound   if the user or role ID provided does not exist
     * @throws RuntimeException if an error occurs during the creation process
     */
    @Transactional
    public ShiftDto create(ShiftDto shiftDto) {
        validateUserId(shiftDto.userId());
        validateShiftTimes(shiftDto);
        User user = getUserById(shiftDto.userId(), repositoryCollector);
        Role role = getRoleByName(shiftDto.roleName());
        return createNewShift(shiftDto, user, role);
    }

    /**
     * Updates an existing Shift or creates a new one if it doesn't exist.
     *
     * @param id       the ID of the shift to update or create
     * @param shiftDto the ShiftDto containing the details of the shift
     * @return the updated or newly created ShiftDto
     * @throws EntityNotFound   if the user or role ID provided does not exist
     * @throws RuntimeException if an error occurs during the update or creation process
     */
    @Transactional
    public ShiftDto update(long id, ShiftDto shiftDto) {
        validateUserId(shiftDto.userId());
        Shift existingShift = findExistingShift(id);
        User user = getUserById(shiftDto.userId(), repositoryCollector);
        Role role = getRoleByName(shiftDto.roleName());

        if (existingShift == null) {
            validateShiftTimes(shiftDto);
            return createNewShift(shiftDto, user, role);
        } else {
            return updateExistingShift(existingShift.getId(), shiftDto, user, role);
        }
    }

    /**
     * Retrieves a User entity by its ID.
     *
     * @param id                  the ID of the user to retrieve
     * @param repositoryCollector the RepositoryCollector object to use for the retrieval
     * @return the User entity with the specified ID
     * @throws IllegalArgumentException if the user with the specified ID does not exist
     */
    private User getUserById(long id, RepositoryCollector repositoryCollector) {
        return repositoryCollector.getUsers().findByIdAndIsActive(id, true)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    /**
     * Finds an existing Shift by its ID.
     *
     * @param id the ID of the shift to find
     * @return the Shift object if found, otherwise null
     */
    private Shift findExistingShift(long id) {
        return repositoryCollector.getShifts().findById(id).orElse(null);
    }

    /**
     * Creates a new Shift entity from the provided ShiftDto, User, and Role.
     *
     * @param shiftDto the ShiftDto containing the details of the shift to create
     * @param user     the User entity associated with the shift
     * @param role     the Role entity associated with the shift
     * @return the created ShiftDto
     */
    private ShiftDto createNewShift(ShiftDto shiftDto, User user, Role role) {
        Shift newShift = shiftDto.toEntity(user, role);
        Shift savedShift = repositoryCollector.getShifts().save(newShift);
        return ShiftDto.fromEntity(savedShift);
    }

    /**
     * Updates an existing Shift entity with the provided details.
     *
     * @param existingShiftId the ID of the existing shift to update
     * @param shiftDto        the ShiftDto containing the updated details of the shift
     * @param user            the User entity associated with the shift
     * @param role            the Role entity associated with the shift
     * @return the updated ShiftDto
     */
    private ShiftDto updateExistingShift(long existingShiftId, ShiftDto shiftDto, User user, Role role) {
        Shift updatedShift = shiftDto.toEntity(user, role);
        updatedShift.setId(existingShiftId);
        updatedShift.setPublished(false);
        Shift savedShift = repositoryCollector.getShifts().save(updatedShift);
        return ShiftDto.fromEntity(savedShift);
    }

    /**
     * Publishes shifts within the specified date range.
     *
     * @param start the start date of the range
     * @param end   the end date of the range
     * @return a list of ShiftDto containing the details of the published shifts
     */
    @Transactional
    public List<ShiftDto> publish(LocalDate start, LocalDate end) {
        List<Shift> shifts = findShiftsByDateRange(start, end);
        publishShifts(shifts);
        List<Shift> updatedShifts = saveShifts(shifts);
        notifyPublishedShifts(updatedShifts);

        return updatedShifts.stream()
                .map(ShiftDto::fromEntity)
                .toList();
    }

    /**
     * Finds shifts within the specified date range.
     *
     * @param start the start date of the range
     * @param end   the end date of the range
     * @return a list of Shift objects within the specified date range
     */
    private List<Shift> findShiftsByDateRange(LocalDate start, LocalDate end) {
        return repositoryCollector.getShifts()
                .findAllByDateRange(start.atStartOfDay(), end.atTime(23, 59, 59));
    }

    /**
     * Marks all shifts in the provided list as published.
     *
     * @param shifts the list of shifts to be marked as published
     */
    private void publishShifts(List<Shift> shifts) {
        shifts.forEach(shift -> shift.setPublished(true));
    }

    /**
     * Saves the provided list of shifts and flushes the changes immediately.
     *
     * @param shifts the list of Shift objects to be saved
     * @return the list of saved Shift objects
     */
    private List<Shift> saveShifts(List<Shift> shifts) {
        return repositoryCollector.getShifts().saveAllAndFlush(shifts);
    }

    /**
     * Sends notifications for all published shifts in the provided list.
     *
     * @param shifts the list of shifts that have been published
     */
    private void notifyPublishedShifts(List<Shift> shifts) {
        shifts.forEach(this::publishedShiftNotification);
    }

    /**
     * Deletes a Shift by its ID.
     *
     * @param id the ID of the Shift to be deleted
     * @throws EntityNotFound if the shift with the specified ID does not exist
     */
    public void delete(long id) throws EntityNotFound {
        Shift shift = findExistingShift(id);
        if (shift == null) {
            throw new EntityNotFound("Nie znaleziono zmiany o id: " + id);
        }
        if (shift.getPublished()) {
            deletedShiftNotification(shift);
        }
        repositoryCollector.getShifts().deleteById(id);
    }

    /**
     * Validates that the shift start and end times are in the future.
     *
     * @param shiftDto the ShiftDto containing the shift times to validate
     * @throws InvalidDateException if the shift start or end time is in the past
     */
    private void validateShiftTimes(ShiftDto shiftDto) {
        if (shiftDto.start().isBefore(LocalDateTime.now()) || shiftDto.end().isBefore(LocalDateTime.now())) {
            throw new InvalidDateException("Data nie może być w przeszłości");
        }
    }

    /**
     * Retrieves a Role entity by its name.
     *
     * @param roleName the name of the role to retrieve
     * @return the Role entity with the specified name
     */
    private Role getRoleByName(String roleName) {
        return repositoryCollector.getRoles().findByName(roleName);
    }

    //TODO: Maybe move this to a separate class

    /**
     * Sends a notification to the user when a shift is published.
     *
     * @param publishedShift the Shift object that was published
     */
    private void publishedShiftNotification(@NotNull Shift publishedShift) {
        NotificationDto notificationDto = ShiftNotificationManager.createPublishNotificationDto(publishedShift);
        notificationService.create(notificationDto);
    }

    /**
     * Sends a notification to the user when a shift is deleted.
     *
     * @param shift the Shift object that was deleted
     */
    private void deletedShiftNotification(@NotNull Shift shift) {
        NotificationDto notificationDto = ShiftNotificationManager.createDeletedNotificationDto(shift);
        notificationService.create(notificationDto);
    }
}