package org.harmoniapp.harmoniwebapi.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.autoscheduling.*;
import org.harmoniapp.harmonidata.entities.PredefineShift;
import org.harmoniapp.harmonidata.entities.Role;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.AiSchedule.AiSchedulerResponse;
import org.harmoniapp.harmoniwebapi.contracts.AiSchedule.ReqRoleDto;
import org.harmoniapp.harmoniwebapi.contracts.AiSchedule.ReqShiftDto;
import org.harmoniapp.harmoniwebapi.contracts.AiSchedule.ScheduleRequirement;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AiScheduleService {
    private final RepositoryCollector repositoryCollector;
    private List<User> userCache;
    private List<Role> roleCache;
    private List<PredefineShift> predefineShiftCache;

    public AiSchedulerResponse generateSchedule(List<ScheduleRequirement> requirementsDto) {
        //TODO: check if number employees in requirements is correct
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();

        predefineShiftCache = repositoryCollector.getPredefineShifts().findAll();

        requirementsDto.sort(Comparator.comparing(ScheduleRequirement::date));
        List<Shift> shifts = prepareShifts(requirementsDto);
        Map<String, List<Employee>> employees = prepareEmployees(requirementsDto.getFirst().date(), requirementsDto.getLast().date());
        Chromosome chromosome = geneticAlgorithm.run(shifts, employees);
        if (chromosome.getFitness() < 0.9) {
            return new AiSchedulerResponse(
                    "Nie udało się wygenerować grafiku, spróbuj ponownie", false
            );
        }

        List<org.harmoniapp.harmonidata.entities.Shift> decodedShifts = decodeShifts(chromosome.getGens());
        repositoryCollector.getShifts().saveAll(decodedShifts);

        userCache = null;
        roleCache = null;

        return new AiSchedulerResponse(
                "Układanie grafiku zakończone pomyślnie", true
        );
    }

    private Map<String, List<Employee>> prepareEmployees(LocalDate start, LocalDate end) {
        userCache = repositoryCollector.getUsers().findAllActiveWithoutAbsenceInDateRange(start, end);

        List<Employee> employees = new ArrayList<>(userCache.size());
        for (User user : userCache) {
            Employee employee = new Employee(user.getEmployeeId(), user.getRoles().getFirst().getName());
            employees.add(employee);
        }
        return employees.stream().collect(Collectors.groupingBy(Employee::getRole));
    }

    private List<Shift> prepareShifts(List<ScheduleRequirement> scheduleRequirements) {
        List<Shift> shifts = new ArrayList<>();

//        scheduleRequirements.sort(Comparator.comparing(ScheduleRequirement::date));
        for (ScheduleRequirement scheduleRequirement : scheduleRequirements) {

            scheduleRequirement.shifts()
                    .sort(Comparator.comparing(rs -> predefineShiftCache.stream()
                            .filter(ps -> ps.getId().equals((long) rs.shiftId()))
                            .findFirst()
                            .orElseThrow()
                            .getStart())
                    );
            for (ReqShiftDto reqShiftDto : scheduleRequirement.shifts()) {
                List<Requirements> requirements = prepareRequirements(reqShiftDto.roles());
                shifts.add(new Shift(reqShiftDto.shiftId().intValue(),
                        scheduleRequirement.date().getDayOfYear(),
                        requirements));
            }
        }
        return shifts;
    }

    private List<Requirements> prepareRequirements(List<ReqRoleDto> requirements) {
        roleCache = repositoryCollector.getRoles().findAll();
        List<Requirements> req = new ArrayList<>(requirements.size());
        for (ReqRoleDto reqRoleDto : requirements) {
            Role role = roleCache.stream().filter(r -> Objects.equals(r.getId(), reqRoleDto.roleId())).findFirst().orElseThrow();
            req.add(new Requirements(role.getName(), reqRoleDto.quantity()));
        }
        return req;
    }

    private List<org.harmoniapp.harmonidata.entities.Shift> decodeShifts(List<Shift> shifts) {
        List<org.harmoniapp.harmonidata.entities.Shift> decodedShiftList = new ArrayList<>(shifts.size());
        LocalDate now = LocalDate.now();

        for (Shift shift : shifts) {
            PredefineShift predShift = predefineShiftCache.stream()
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
                decodedShift.setUser(userCache.stream()
                        .filter(u -> u.getEmployeeId().equals(employee.getId()))
                        .findFirst()
                        .orElseThrow());
                decodedShift.setRole(roleCache.stream()
                        .filter(r -> r.getName().equals(employee.getRole()))
                        .findFirst()
                        .orElseThrow());
                decodedShift.setPublished(false);
                decodedShiftList.add(decodedShift);
            }
        }
        return decodedShiftList;
    }
}
