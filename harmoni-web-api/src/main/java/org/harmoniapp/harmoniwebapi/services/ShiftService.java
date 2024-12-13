package org.harmoniapp.harmoniwebapi.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.NotificationType;
import org.harmoniapp.harmonidata.entities.Role;
import org.harmoniapp.harmonidata.entities.Shift;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.NotificationDto;
import org.harmoniapp.harmoniwebapi.contracts.ShiftDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
     * @throws IllegalArgumentException if the user or role ID provided does not exist or if the shift start and end times are in the past
     * @throws RuntimeException if an error occurs during creation
     */
    public ShiftDto createShift(ShiftDto shiftDto) {
        try {
            if(shiftDto.start().isBefore(LocalDateTime.now()) || shiftDto.end().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Shift start and end times must be in the future");
            }
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
                if(shiftDto.start().isBefore(LocalDateTime.now()) || shiftDto.end().isBefore(LocalDateTime.now())) {
                    throw new IllegalArgumentException("Shift start and end times must be in the future");
                }
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
     * Publishes all shifts within the specified date range by setting their 'published' status to true.
     *
     * @param start the start date of the range
     * @param end   the end date of the range
     * @return a list of ShiftDto containing the details of the published shifts
     * @throws RuntimeException if an error occurs while publishing shifts
     */
    @Transactional
    public List<ShiftDto> publishShifts(LocalDate start, LocalDate end) {
        List<Shift> shifts = repositoryCollector.getShifts().findAllByDateRange(start.atStartOfDay(), end.atTime(23, 59, 59));
        shifts.forEach(shift -> shift.setPublished(true));

        List<Shift> updatedShifts = repositoryCollector.getShifts().saveAllAndFlush(shifts);
        updatedShifts.forEach(this::publishedShiftNotification);

        return updatedShifts.stream()
                .map(ShiftDto::fromEntity)
                .toList();
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

            if (shift.getPublished()) {
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
     * @param publishedShift the Shift object that was published
     * @throws RuntimeException if the shift or notification type is not found
     */
    private void publishedShiftNotification(Shift publishedShift) {
        NotificationType notificationType = repositoryCollector.getNotificationTypes().findById(1L) //1 is Shift Published
                .orElseThrow(() -> new RuntimeException("Notification type not found"));

        NotificationDto notificationDto = new NotificationDto(
                0L, // id is set automatically by the database
                publishedShift.getUser().getId(),
                "Nowa zmiana opublikowana",
                "Nowa zmiana opublikowana. Zmiana " +
                        publishedShift.getStart() + " - " + publishedShift.getEnd() +
                        "opublikowana. Zapoznaj sie ze swoim grafikiem.",
                notificationType.getTypeName(),
                false,
                LocalDateTime.now()
        );

        notificationService.createNotification(notificationDto);
    }

    /**
     * Sends a notification to the user when a shift is deleted.
     *
     * @param shift the deleted shift object
     * @throws RuntimeException if the shift or notification type is not found
     */
    private void deletedShiftNotification(Shift shift) {
        NotificationType notificationType = repositoryCollector.getNotificationTypes().findById(6L) //6 is Shift Deleted
                .orElseThrow(() -> new RuntimeException("Notification type not found"));

        Shift publishedShift = repositoryCollector.getShifts().findById(shift.getId())
                .orElseThrow(() -> new RuntimeException("Shift not found"));

        NotificationDto notificationDto = new NotificationDto(
                0L, // id is set automatically by the database
                publishedShift.getUser().getId(),
                "Zmiana usunięta",
                "Zmiana usunięta. Zmiana " +
                        publishedShift.getStart() + " - " + publishedShift.getEnd() +
                        " usunięta. Zapoznaj sie ze swoim grafikiem.",
                notificationType.getTypeName(),
                false,
                LocalDateTime.now()
        );

        notificationService.createNotification(notificationDto);
    }

}
