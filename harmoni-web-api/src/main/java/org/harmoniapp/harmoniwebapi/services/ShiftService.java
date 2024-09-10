package org.harmoniapp.harmoniwebapi.services;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.NotificationType;
import org.harmoniapp.harmonidata.entities.Role;
import org.harmoniapp.harmonidata.entities.Shift;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.NotificationDto;
import org.harmoniapp.harmoniwebapi.contracts.ShiftDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing shifts.
 * Provides methods to retrieve shift information.
 */
@Service
@RequiredArgsConstructor
@ComponentScan(basePackages = {"org.harmoniapp.harmonidata"})
public class ShiftService {
    private final RepositoryCollector repositoryCollector;
    private final NotificationService notificationService;

    /**
     * Retrieves a ShiftDto for the shift with the specified ID.
     *
     * @param id the ID of the shift to retrieve
     * @return a ShiftDto containing the details of the shift
     * @throws IllegalArgumentException if the shift with the specified ID does not exist
     */
    public ShiftDto getShift(long id) {
        try {
            Shift shift = repositoryCollector.getShifts().findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Shift not found"));

            return ShiftDto.fromEntity(shift);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a list of ShiftDto for the shifts within the specified date range.
     *
     * @param start the start date of the range
     * @param end   the end date of the range
     * @return a list of ShiftDto containing the details of shifts within the date range
     * @throws RuntimeException if an error occurs while retrieving shifts
     */
    public List<ShiftDto> getShiftsByDateRangeAndUserId(LocalDateTime start, LocalDateTime end, Long userId) {
        try {
            List<Shift> shifts = repositoryCollector.getShifts().findAllByDateRangeAndUserId(start, end, userId);
            return shifts.stream()
                    .map(ShiftDto::fromEntity)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

    /**
     * Creates a new Shift from a ShiftDto.
     *
     * @param shiftDto the ShiftDto containing the details of the shift to create
     * @return the created ShiftDto
     * @throws IllegalArgumentException if the user or role ID provided does not exist
     * @throws RuntimeException if an error occurs during creation
     */
    public ShiftDto createShift(ShiftDto shiftDto) {
        try {
            User user = repositoryCollector.getUsers().findById(shiftDto.userId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            Role role = repositoryCollector.getRoles().findByName(shiftDto.roleName());

            Shift shift = shiftDto.toEntity(user, role);
            Shift savedShift = repositoryCollector.getShifts().save(shift);
            return ShiftDto.fromEntity(savedShift);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing Shift or creates a new one if it doesn't exist.
     *
     * @param id       the ID of the shift to update or create
     * @param shiftDto the ShiftDto containing the details of the shift
     * @return the updated or newly created ShiftDto
     * @throws IllegalArgumentException if the user or role ID provided does not exist
     * @throws RuntimeException if an error occurs during the update or creation process
     */
    @Transactional
    public ShiftDto updateShift(long id, ShiftDto shiftDto) {
        try {
            Shift existingShift = repositoryCollector.getShifts().findById(id)
                    .orElse(null);

            User user = repositoryCollector.getUsers().findById(shiftDto.userId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            Role role = repositoryCollector.getRoles().findByName(shiftDto.roleName());

            if (existingShift == null) {
                Shift newShift = new Shift(id, shiftDto.start(), shiftDto.end(), user, role, false);
                Shift savedShift = repositoryCollector.getShifts().save(newShift);
                return ShiftDto.fromEntity(savedShift);
            } else {
                existingShift.setStart(shiftDto.start());
                existingShift.setEnd(shiftDto.end());
                existingShift.setUser(user);
                existingShift.setRole(role);
                existingShift.setPublished(false);
                Shift updatedShift = repositoryCollector.getShifts().save(existingShift);
                return ShiftDto.fromEntity(updatedShift);
            }
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

    /**
     * Publishes an existing shift by setting its 'published' status to true.
     *
     * @param shiftId the ID of the shift to publish
     * @return the updated ShiftDto after setting the published status to true
     * @throws IllegalArgumentException if the shift with the given ID is not found
     */
    @Transactional
    public ShiftDto publishShift(long shiftId) {
        Shift existingShift = repositoryCollector.getShifts().findById(shiftId)
                .orElseThrow(() -> new IllegalArgumentException("Shift not found"));

        existingShift.setPublished(true);
        Shift updatedShift = repositoryCollector.getShifts().save(existingShift);

        publishedShiftNotification(shiftId);

        return ShiftDto.fromEntity(updatedShift);
    }

    /**
     * Deletes a Shift by its ID.
     *
     * @param id the ID of the Shift to be deleted
     * @throws RuntimeException if an error occurs during deletion
     */
    public void deleteShift(long id) {
        try {
            Shift shift = repositoryCollector.getShifts().findById(id)
                    .orElseThrow(() -> new RuntimeException("Shift not found"));

            if (shift.isPublished()) {
                deletedShiftNotification(shift);
            }

            repositoryCollector.getShifts().deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

    /**
     * Sends a notification to the user when a new shift is published.
     *
     * @param shiftId the ID of the published shift
     * @throws RuntimeException if the shift or notification type is not found
     */
    private void publishedShiftNotification(long shiftId) {
        NotificationType notificationType = repositoryCollector.getNotificationTypes().findById(1L) //1 is Shift Published
                .orElseThrow(() -> new RuntimeException("Notification type not found"));

        Shift publishedShift = repositoryCollector.getShifts().findById(shiftId)
                .orElseThrow(() -> new RuntimeException("Shift not found"));

        NotificationDto notificationDto = new NotificationDto(
                0L, // id is set automatically by the database
                publishedShift.getUser().getId(),
                "New Shift Published",
                "New shift published. Shift " +
                        publishedShift.getStart() + " - " + publishedShift.getEnd() +
                        " is published. Please check your schedule.",
                notificationType.getTypeName(),
                false,
                LocalDateTime.now()
        );

        notificationService.createNotification(notificationDto);
    }

    private void deletedShiftNotification(Shift shift) {
        NotificationType notificationType = repositoryCollector.getNotificationTypes().findById(6L) //6 is Shift Deleted
                .orElseThrow(() -> new RuntimeException("Notification type not found"));

        Shift publishedShift = repositoryCollector.getShifts().findById(shift.getId())
                .orElseThrow(() -> new RuntimeException("Shift not found"));

        NotificationDto notificationDto = new NotificationDto(
                0L, // id is set automatically by the database
                publishedShift.getUser().getId(),
                "Shift Was Deleted",
                "Shift Was Deleted. Shift " +
                        publishedShift.getStart() + " - " + publishedShift.getEnd() +
                        " was deleted. Please check your schedule.",
                notificationType.getTypeName(),
                false,
                LocalDateTime.now()
        );

        notificationService.createNotification(notificationDto);
    }

}
