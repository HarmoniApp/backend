package org.harmoniapp.harmoniwebapi.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.autoscheduling.*;
import org.harmoniapp.harmonidata.entities.PredefineShift;
import org.harmoniapp.harmonidata.entities.Role;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.AiSchedule.*;
import org.harmoniapp.harmoniwebapi.exception.NotEnoughEmployees;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Service for AI schedule generation.
 * Provides methods for generating schedules based on requirements.
 */
@Service
@RequiredArgsConstructor
public class AiScheduleService {
    private final RepositoryCollector repositoryCollector;
    ;

    /**
     * Generates a schedule based on the specified requirements.
     *
     * @param requirementsDto the list of schedule requirements to generate the schedule from
     * @return an AiSchedulerResponse containing the generated schedule
     */
    public AiSchedulerResponse generateSchedule(List<ScheduleRequirement> requirementsDto) {
        requirementsDto.sort(Comparator.comparing(ScheduleRequirement::date));

        List<User> users = repositoryCollector.getUsers().findAllActiveWithoutAbsenceInDateRange(
                requirementsDto.getFirst().date(), requirementsDto.getLast().date());
        List<PredefineShift> predefineShifts = repositoryCollector.getPredefineShifts().findAll();
        List<Role> roles = repositoryCollector.getRoles().findAll();

        Map<String, List<Employee>> employees = prepareEmployees(requirementsDto, users);
        try {
            verifyUserQuantity(requirementsDto, employees, roles);
        } catch (NotEnoughEmployees e) {
            throw e;
        }

        List<Shift> shifts = prepareShifts(requirementsDto, predefineShifts, roles);

        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
        Chromosome chromosome = geneticAlgorithm.run(shifts, employees);
        if (chromosome.getFitness() < 0.9) {
            return new AiSchedulerResponse(
                    "Nie udało się wygenerować grafiku, spróbuj ponownie", false
            );
        }

        List<org.harmoniapp.harmonidata.entities.Shift> decodedShifts = decodeShifts(chromosome.getGens(), users, predefineShifts, roles);
        repositoryCollector.getShifts().saveAll(decodedShifts);

        return new AiSchedulerResponse(
                "Układanie grafiku zakończone pomyślnie", true
        );
    }

    /**
     * Prepares employees for the schedule generation.
     *
     * @param requirements the list of schedule requirements
     * @return a map of employees grouped by role
     */
    private Map<String, List<Employee>> prepareEmployees(List<ScheduleRequirement> requirements, List<User> users) {
        Set<Long> validRoles = requirements.stream()
                .flatMap(scheduleRequirement -> scheduleRequirement.shifts().stream())
                .flatMap(reqShiftDto -> reqShiftDto.roles().stream())
                .map(ReqRoleDto::roleId)
                .collect(Collectors.toSet());

        List<Employee> employees = new ArrayList<>();
        for (User user : users) {
            List<Role> userRoles = user.getRoles();
            for (Role role : userRoles) {
                if (validRoles.contains(role.getId())) {
                    Employee employee = new Employee(user.getEmployeeId(), role.getName());
                    employees.add(employee);
                    break;
                }
            }
        }
        return employees.stream().collect(Collectors.groupingBy(Employee::getRole));
    }

    /**
     * Prepares shifts for the schedule generation.
     *
     * @param scheduleRequirements the list of schedule requirements
     * @return a list of shifts
     */
    private List<Shift> prepareShifts(List<ScheduleRequirement> scheduleRequirements, List<PredefineShift> predefineShifts,
                                      List<Role> roles) {
        List<Shift> shifts = new ArrayList<>();

        for (ScheduleRequirement scheduleRequirement : scheduleRequirements) {

            scheduleRequirement.shifts()
                    .sort(Comparator.comparing(rs -> predefineShifts.stream()
                            .filter(ps -> ps.getId().equals((long) rs.shiftId()))
                            .findFirst()
                            .orElseThrow()
                            .getStart())
                    );
            for (ReqShiftDto reqShiftDto : scheduleRequirement.shifts()) {
                List<Requirements> requirements = prepareRequirements(reqShiftDto.roles(), roles);
                shifts.add(new Shift(reqShiftDto.shiftId().intValue(),
                        scheduleRequirement.date().getDayOfYear(),
                        requirements));
            }
        }
        return shifts;
    }

    /**
     * Prepares requirements for the schedule generation.
     *
     * @param requirements the list of requirements
     * @return a list of requirements
     */
    private List<Requirements> prepareRequirements(List<ReqRoleDto> requirements, List<Role> roles) {
        List<Requirements> req = new ArrayList<>(requirements.size());
        for (ReqRoleDto reqRoleDto : requirements) {
            Role role = roles.stream().filter(r -> Objects.equals(r.getId(), reqRoleDto.roleId())).findFirst().orElseThrow();
            req.add(new Requirements(role.getName(), reqRoleDto.quantity()));
        }
        return req;
    }

    /**
     * Decodes shifts from shift representation in algorithm to entity.
     *
     * @param shifts the list of shifts
     * @return a list of decoded shifts
     */
    private List<org.harmoniapp.harmonidata.entities.Shift> decodeShifts(List<Shift> shifts, List<User> users,
                                                                         List<PredefineShift> predefineShifts, List<Role> roles) {
        List<org.harmoniapp.harmonidata.entities.Shift> decodedShiftList = new ArrayList<>(shifts.size());
        LocalDate now = LocalDate.now();

        for (Shift shift : shifts) {
            PredefineShift predShift = predefineShifts.stream()
                    .filter(ps -> ps.getId().equals((long) shift.getId()))
                    .findFirst()
                    .orElseThrow();
            LocalDate date = LocalDate.ofYearDay(
                    (now.getDayOfYear() <= shift.getDay()) ? now.getYear() : now.getYear() + 1, shift.getDay());
            LocalDateTime start = LocalDateTime.of(date, predShift.getStart());
            LocalDateTime end = LocalDateTime.of(
                    (predShift.getStart().isBefore(predShift.getEnd())) ? date : date.plusDays(1), predShift.getEnd()
            );

            for (Employee employee : shift.getEmployees()) {
                org.harmoniapp.harmonidata.entities.Shift decodedShift = new org.harmoniapp.harmonidata.entities.Shift();
                decodedShift.setStart(start);
                decodedShift.setEnd(end);
                decodedShift.setUser(users.stream()
                        .filter(u -> u.getEmployeeId().equals(employee.getId()))
                        .findFirst()
                        .orElseThrow());
                decodedShift.setRole(roles.stream()
                        .filter(r -> r.getName().equals(employee.getRole()))
                        .findFirst()
                        .orElseThrow());
                decodedShift.setPublished(false);
                decodedShiftList.add(decodedShift);
            }
        }
        return decodedShiftList;
    }

    /**
     * Verifies if there are enough employees to generate a schedule.
     *
     * @param requirementsDto the list of schedule requirements
     * @param employees       the map of employees grouped by role
     * @throws NotEnoughEmployees if there are not enough employees to generate a schedule
     */
    private void verifyUserQuantity(List<ScheduleRequirement> requirementsDto, Map<String, List<Employee>> employees,
                                    List<Role> roles) throws NotEnoughEmployees {
        Map<String, Integer> required = summarizeRequiredEmployees(requirementsDto, roles);
        Map<String, Integer> available = employees.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size() * 5));
        required.forEach((role, requiredCount) -> {
            int availableCount = available.getOrDefault(role, 0);
            if (availableCount < requiredCount) {
                String message = String.format("Za mało pracowników o roli %s, wymagane zmiany do obsadzenia: %d, możliwe zmiany do obsadzenia: %d",
                        role, requiredCount, availableCount);
                throw new NotEnoughEmployees(message);
            }
        });
    }

    /**
     * Summarizes required employees from the list of schedule requirements.
     *
     * @param requirementsDto the list of schedule requirements
     * @return a map of roles and required employees
     */
    private Map<String, Integer> summarizeRequiredEmployees(List<ScheduleRequirement> requirementsDto, List<Role> roles) {
        return requirementsDto.stream()
                .flatMap(scheduleRequirement -> scheduleRequirement.shifts().stream())
                .flatMap(reqShiftDto -> reqShiftDto.roles().stream())
                .collect(Collectors.toMap(
                        reqRoleDto -> roles.stream()
                                .filter(r -> r.getId().equals(reqRoleDto.roleId()))
                                .findFirst()
                                .orElseThrow()
                                .getName(),
                        ReqRoleDto::quantity,
                        Integer::sum
                ));
    }
}
