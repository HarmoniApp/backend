package org.harmoniapp.services.schedule.aischedule;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.schedule.aischedule.AggregatedScheduleData;
import org.harmoniapp.contracts.schedule.aischedule.ReqRoleDto;
import org.harmoniapp.contracts.schedule.aischedule.ReqShiftDto;
import org.harmoniapp.contracts.schedule.aischedule.ScheduleRequirement;
import org.harmoniapp.entities.profile.Role;
import org.harmoniapp.entities.schedule.PredefineShift;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.exception.EntityNotFoundException;
import org.harmoniapp.exception.InvalidAiScheduleRequirementsException;
import org.harmoniapp.geneticalgorithm.Employee;
import org.harmoniapp.geneticalgorithm.Gen;
import org.harmoniapp.geneticalgorithm.Requirements;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the ScheduleDataEncoder interface.
 * This class is responsible for encoding schedule data.
 */
@Component
@RequiredArgsConstructor
public class ScheduleDataEncoderImpl implements ScheduleDataEncoder {
    private final RepositoryCollector repositoryCollector;

    /**
     * Prepares the data required for schedule generation.
     *
     * @param requirementsDto the list of schedule requirements
     * @return an AggregatedScheduleData object containing users, predefined shifts, roles, employees, and shifts
     */
    @Override
    public AggregatedScheduleData prepareData(List<ScheduleRequirement> requirementsDto) {
        validAndSortRequirements(requirementsDto);
        requirementsDto.sort(Comparator.comparing(ScheduleRequirement::date));
        List<User> users = findActiveUsersWithoutAbsence(requirementsDto);
        Map<String, List<Employee>> employees = prepareEmployees(requirementsDto, users);
        List<Role> roles = repositoryCollector.getRoles().findAll();
        verifyUserQuantity(requirementsDto, employees, roles);

        List<PredefineShift> predefineShifts = repositoryCollector.getPredefineShifts().findAll();
        List<Gen> shifts = prepareShifts(requirementsDto, predefineShifts, roles);

        return new AggregatedScheduleData(users, predefineShifts, roles, employees, shifts);
    }

    /**
     * Validates the schedule requirements and sorts them by date.
     *
     * @param requirementsDto the list of schedule requirements
     * @throws InvalidAiScheduleRequirementsException if there is more than one requirement for a single day
     */
    void validAndSortRequirements(List<ScheduleRequirement> requirementsDto) {
        requirementsDto.sort(Comparator.comparing(ScheduleRequirement::date));
        for (int i = 0; i < requirementsDto.size() - 1; i++) {
            if (requirementsDto.get(i).date().equals(requirementsDto.get(i + 1).date())) {
                throw new InvalidAiScheduleRequirementsException(
                        "Data %s została podana wiele razy". formatted(requirementsDto.get(i).date()));
            }
        }
    }

    /**
     * Finds all active users who do not have any absences within the date range specified by the schedule requirements.
     *
     * @param requirementsDto the list of schedule requirements
     * @return a list of active users without absences in the specified date range
     */
    List<User> findActiveUsersWithoutAbsence(List<ScheduleRequirement> requirementsDto) {
        return repositoryCollector.getUsers().findAllActiveWithoutAbsenceInDateRange(
                requirementsDto.getFirst().date(), requirementsDto.getLast().date());
    }

    /**
     * Prepares employees for the schedule generation.
     *
     * @param requirements the list of schedule requirements
     * @param users        the list of active users
     * @return a map of employees grouped by role
     */
    Map<String, List<Employee>> prepareEmployees(List<ScheduleRequirement> requirements, List<User> users) {
        Set<Long> validRoles = getValidRoles(requirements);
        List<Employee> employees = getUniqueEmployees(users, validRoles);
        return employees.stream().collect(Collectors.groupingBy(Employee::role));
    }

    /**
     * Extracts the set of valid role IDs from the given list of schedule requirements.
     *
     * @param requirements the list of schedule requirements
     * @return a set of valid role IDs
     */
    Set<Long> getValidRoles(List<ScheduleRequirement> requirements) {
        return requirements.stream()
                .flatMap(scheduleRequirement -> scheduleRequirement.shifts().stream())
                .flatMap(reqShiftDto -> reqShiftDto.roles().stream())
                .map(ReqRoleDto::roleId)
                .collect(Collectors.toSet());
    }

    /**
     * Retrieves a list of unique employees based on the provided users and valid roles.
     *
     * @param users      the list of active users
     * @param validRoles the set of valid role IDs
     * @return a list of unique employees
     */
    List<Employee> getUniqueEmployees(List<User> users, Set<Long> validRoles) {
        List<Employee> employees = new ArrayList<>();
        for (User user : users) {
            Set<Role> userRoles = user.getRoles();
            for (Role role : userRoles) {
                if (validRoles.contains(role.getId())) {
                    Employee employee = new Employee(user.getEmployeeId(), role.getName());
                    employees.add(employee);
                    break;
                }
            }
        }
        return employees;
    }

    /**
     * Verifies if there are enough employees to generate a schedule.
     *
     * @param requirementsDto the list of schedule requirements
     * @param employees       the map of employees grouped by role
     * @param roles           the list of roles
     * @throws InvalidAiScheduleRequirementsException if there are not enough employees to generate a schedule
     */
    void verifyUserQuantity(List<ScheduleRequirement> requirementsDto, Map<String, List<Employee>> employees,
                            List<Role> roles) throws InvalidAiScheduleRequirementsException {
        Map<String, Integer> required = summarizeRequiredEmployees(requirementsDto, roles);
        Map<String, Integer> available = calculateAvailableEmployees(requirementsDto, employees);
        checkEmployeeAvailability(required, available);
    }

    /**
     * Summarizes required employees from the list of schedule requirements.
     *
     * @param requirementsDto the list of schedule requirements
     * @param roles           the list of roles
     * @return a map of roles and required employees
     */
    Map<String, Integer> summarizeRequiredEmployees(List<ScheduleRequirement> requirementsDto, List<Role> roles) {
        return requirementsDto.stream()
                .flatMap(scheduleRequirement -> scheduleRequirement.shifts().stream())
                .flatMap(reqShiftDto -> reqShiftDto.roles().stream())
                .collect(Collectors.toMap(
                        reqRoleDto -> findRoleNameById(roles, reqRoleDto.roleId()),
                        ReqRoleDto::quantity,
                        Integer::sum
                ));
    }

    /**
     * Finds the role name by its ID.
     *
     * @param roles  the list of roles
     * @param roleId the ID of the role to find
     * @return the name of the role
     * @throws EntityNotFoundException if no role with the given ID is found
     */
    String findRoleNameById(List<Role> roles, Long roleId) {
        return roles.stream()
                .filter(r -> r.getId().equals(roleId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono roli o id: " + roleId))
                .getName();
    }

    /**
     * Calculates the available employees for the schedule generation.
     *
     * @param requirementsDto the list of schedule requirements
     * @param employees       the map of employees grouped by role
     * @return a map of roles and available employees
     */
    Map<String, Integer> calculateAvailableEmployees(List<ScheduleRequirement> requirementsDto, Map<String, List<Employee>> employees) {
        return employees.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> {
                    int multiplier = (requirementsDto.size() < 5) ? requirementsDto.size() : (5 * (requirementsDto.size() / 7 + 1));
                    return e.getValue().size() * multiplier;
                }));
    }


    /**
     * Checks if there are enough employees available to meet the schedule requirements.
     *
     * @param required  a map of roles and the number of required employees for each role
     * @param available a map of roles and the number of available employees for each role
     * @throws InvalidAiScheduleRequirementsException if there are not enough employees available to meet the requirements
     */
    void checkEmployeeAvailability(Map<String, Integer> required, Map<String, Integer> available) throws InvalidAiScheduleRequirementsException {
        required.forEach((role, requiredCount) -> {
            int availableCount = available.getOrDefault(role, 0);
            if (availableCount < requiredCount) {
                String message = String.format("Za mało pracowników o roli %s, wymagane zmiany do obsadzenia: %d, możliwe zmiany do obsadzenia: %d",
                        role, requiredCount, availableCount);
                throw new InvalidAiScheduleRequirementsException(message);
            }
        });
    }


    /**
     * Prepares shifts for the schedule generation.
     *
     * @param scheduleRequirements the list of schedule requirements
     * @param predefineShifts      the list of predefined shifts
     * @param roles                the list of roles
     * @return a list of shifts
     */
    List<Gen> prepareShifts(List<ScheduleRequirement> scheduleRequirements, List<PredefineShift> predefineShifts,
                            List<Role> roles) {
        List<Gen> shifts = new ArrayList<>();

        for (ScheduleRequirement scheduleRequirement : scheduleRequirements) {
            sortShiftsByStart(scheduleRequirement, predefineShifts);

            for (ReqShiftDto reqShiftDto : scheduleRequirement.shifts()) {
                List<Requirements> requirements = prepareRequirements(reqShiftDto.roles(), roles);
                shifts.add(createGen(reqShiftDto, scheduleRequirement, predefineShifts, requirements));
            }
        }
        return shifts;
    }

    /**
     * Sorts the shifts of the given schedule requirement by their start time.
     *
     * @param scheduleRequirement the schedule requirement containing the shifts to be sorted
     * @param predefineShifts     the list of predefined shifts to determine the start time of each shift
     */
    void sortShiftsByStart(ScheduleRequirement scheduleRequirement, List<PredefineShift> predefineShifts) {
        scheduleRequirement.shifts()
                .sort(Comparator.comparing(rs -> predefineShifts.stream()
                        .filter(ps -> ps.getId().equals(rs.shiftId()))
                        .findFirst()
                        .orElseThrow()
                        .getStart())
                );
    }

    /**
     * Prepares a list of requirements for the schedule generation.
     *
     * @param requirements the list of role requirements
     * @param roles        the list of roles
     * @return a list of requirements
     */
    List<Requirements> prepareRequirements(List<ReqRoleDto> requirements, List<Role> roles) {
        List<Requirements> req = new ArrayList<>(requirements.size());
        for (ReqRoleDto reqRoleDto : requirements) {
            Role role = roles.stream().filter(r -> Objects.equals(r.getId(), reqRoleDto.roleId())).findFirst().orElseThrow();
            req.add(new Requirements(role.getName(), reqRoleDto.quantity()));
        }
        return req;
    }

    /**
     * Creates a Gen object for the schedule generation.
     *
     * @param reqShiftDto         the shift requirement DTO
     * @param scheduleRequirement the schedule requirement
     * @param predefineShifts     the list of predefined shifts
     * @param requirements        the list of requirements
     * @return a Gen object representing the shift
     */
    Gen createGen(ReqShiftDto reqShiftDto, ScheduleRequirement scheduleRequirement,
                  List<PredefineShift> predefineShifts, List<Requirements> requirements) {
        PredefineShift shift = findShiftStartTime(predefineShifts, reqShiftDto);
        return new Gen(reqShiftDto.shiftId().intValue(),
                scheduleRequirement.date().getDayOfYear(),
                shift.getStart(),
                shift.getEnd(),
                null,
                requirements);
    }

    /**
     * Finds the start time of the shift with the given ID.
     *
     * @param predefineShifts the list of predefined shifts
     * @param reqShiftDto     the shift requirement DTO
     * @return the start time of the shift
     */
    PredefineShift findShiftStartTime(List<PredefineShift> predefineShifts, ReqShiftDto reqShiftDto) {
        return predefineShifts.stream()
                .filter(ps -> ps.getId().equals(reqShiftDto.shiftId()))
                .findFirst()
                .orElseThrow();
    }
}
