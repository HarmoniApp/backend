package org.harmoniapp.services.schedule.aischedule;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.schedule.aischedule.AggregatedScheduleData;
import org.harmoniapp.entities.profile.Role;
import org.harmoniapp.entities.schedule.PredefineShift;
import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.exception.EntityNotFound;
import org.harmoniapp.geneticalgorithm.Employee;
import org.harmoniapp.geneticalgorithm.Gen;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the AlgorithmEntityMapper interface.
 * This class provides methods to decode shifts from their algorithmic representation
 * to entity representation.
 */
@Component
@RequiredArgsConstructor
public class AlgorithmEntityMapperImpl implements AlgorithmEntityMapper {

    /**
     * Decodes shifts from shift representation in algorithm to entity.
     *
     * @param shifts the list of shifts
     * @param data   the aggregated schedule data
     * @return a list of decoded shifts
     */
    public List<Shift> decodeShifts(List<Gen> shifts, AggregatedScheduleData data) {
        List<Shift> decodedShiftList = new ArrayList<>(shifts.size());
        LocalDate now = LocalDate.now();

        for (Gen shift : shifts) {
            PredefineShift predShift = findPredefineShift(data.predefineShifts(), shift.id());
            LocalDate date = calculateShiftDate(now, shift.day());
            LocalDateTime start = LocalDateTime.of(date, predShift.getStart());
            LocalDateTime end = calculateShiftEnd(date, predShift);

            decodedShiftList.addAll(createDecodedShifts(start, end, shift.employees(), data));
        }
        return decodedShiftList;
    }

    /**
     * Finds a predefined shift by its ID.
     *
     * @param predefineShifts the list of predefined shifts
     * @param shiftId         the ID of the shift to find
     * @return the predefined shift with the specified ID
     * @throws EntityNotFound if no predefined shift with the specified ID is found
     */
    private PredefineShift findPredefineShift(List<PredefineShift> predefineShifts, long shiftId) {
        return predefineShifts.stream()
                .filter(ps -> ps.getId().equals(shiftId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFound("Nie znaleziono predefiniowanej zmiany o id: " + shiftId));
    }

    /**
     * Calculates the date of the shift based on the current date and the shift day.
     *
     * @param now      the current date
     * @param shiftDay the day of the shift in the year
     * @return the calculated date of the shift
     */
    private LocalDate calculateShiftDate(LocalDate now, int shiftDay) {
        return LocalDate.ofYearDay(
                (now.getDayOfYear() <= shiftDay) ? now.getYear() : now.getYear() + 1, shiftDay);
    }

    /**
     * Calculates the end time of a shift based on the start and end times of the predefined shift.
     *
     * @param date      the date of the shift
     * @param predShift the predefined shift
     * @return the calculated end time of the shift
     */
    private LocalDateTime calculateShiftEnd(LocalDate date, PredefineShift predShift) {
        return LocalDateTime.of(
                (predShift.getStart().isBefore(predShift.getEnd())) ? date : date.plusDays(1), predShift.getEnd()
        );
    }

    /**
     * Creates a list of decoded shifts from the given start and end times, employees, and aggregated schedule data.
     *
     * @param start     the start time of the shift
     * @param end       the end time of the shift
     * @param employees the list of employees assigned to the shift
     * @param data      the aggregated schedule data
     * @return a list of decoded shifts
     */
    private List<Shift> createDecodedShifts(LocalDateTime start, LocalDateTime end, List<Employee> employees, AggregatedScheduleData data) {
        List<Shift> decodedShifts = new ArrayList<>();
        for (Employee employee : employees) {
            Shift decodedShift = createDecodedShift(start, end, employee, data);
            decodedShifts.add(decodedShift);
        }
        return decodedShifts;
    }

    /**
     * Creates a decoded shift from the given start and end times, employee, and aggregated schedule data.
     *
     * @param start    the start time of the shift
     * @param end      the end time of the shift
     * @param employee the employee assigned to the shift
     * @param data     the aggregated schedule data
     * @return the created decoded shift
     */
    private Shift createDecodedShift(LocalDateTime start, LocalDateTime end, Employee employee, AggregatedScheduleData data) {
        Shift decodedShift = new Shift();
        decodedShift.setStart(start);
        decodedShift.setEnd(end);
        decodedShift.setUser(findUserByEmployeeId(data.users(), employee.id()));
        decodedShift.setRole(findRoleByName(data.roles(), employee.role()));
        decodedShift.setPublished(false);
        return decodedShift;
    }

    /**
     * Finds a user by their employee ID.
     *
     * @param users      the list of users
     * @param employeeId the employee ID to search for
     * @return the user with the specified employee ID
     * @throws EntityNotFound if no user with the specified employee ID is found
     */
    private User findUserByEmployeeId(List<User> users, String employeeId) {
        return users.stream()
                .filter(u -> u.getEmployeeId().equals(employeeId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFound("Nie znaleziono użytkownika o id pracownika: " + employeeId));
    }

    /**
     * Finds a role by its name.
     *
     * @param roles    the list of roles
     * @param roleName the name of the role to find
     * @return the role with the specified name
     * @throws EntityNotFound if no role with the specified name is found
     */
    private Role findRoleByName(List<Role> roles, String roleName) {
        return roles.stream()
                .filter(r -> r.getName().equals(roleName))
                .findFirst()
                .orElseThrow(() -> new EntityNotFound("Nie znaleziono roli o nazwie: " + roleName));
    }
}
