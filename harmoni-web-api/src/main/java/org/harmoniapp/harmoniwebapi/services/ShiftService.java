package org.harmoniapp.harmoniwebapi.services;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.Role;
import org.harmoniapp.harmonidata.entities.Shift;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.ShiftDto;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
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
    public List<ShiftDto> getShiftsByDateRange(LocalDate start, LocalDate end, Long userId) {
        try {
            List<Shift> shifts = repositoryCollector.getShifts().findAllByDateRangeAndUserId(
                    start.atStartOfDay(),
                    end.atTime(LocalTime.MAX),
                    userId);
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

            Role role = repositoryCollector.getRoles().findById(shiftDto.roleId())
                    .orElseThrow(() -> new IllegalArgumentException("Role not found"));

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

            Role role = repositoryCollector.getRoles().findById(shiftDto.roleId())
                    .orElseThrow(() -> new IllegalArgumentException("Role not found"));

            if (existingShift == null) {
                Shift newShift = new Shift(id, shiftDto.start().atStartOfDay(), shiftDto.end().atTime(LocalTime.MAX), user, role);
                Shift savedShift = repositoryCollector.getShifts().save(newShift);
                return ShiftDto.fromEntity(savedShift);
            } else {
                existingShift.setStart(shiftDto.start().atStartOfDay());
                existingShift.setEnd(shiftDto.end().atTime(LocalTime.MAX));
                existingShift.setUser(user);
                existingShift.setRole(role);
                Shift updatedShift = repositoryCollector.getShifts().save(existingShift);
                return ShiftDto.fromEntity(updatedShift);
            }
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a Shift by its ID.
     *
     * @param id the ID of the Shift to be deleted
     * @throws RuntimeException if an error occurs during deletion
     */
    public void deleteShift(long id) {
        try {
            repositoryCollector.getShifts().deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

}
