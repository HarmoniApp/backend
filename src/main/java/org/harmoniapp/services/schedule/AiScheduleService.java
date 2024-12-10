package org.harmoniapp.services.schedule;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.schedule.aischedule.*;
import org.harmoniapp.entities.notification.Notification;
import org.harmoniapp.entities.notification.NotificationType;
import org.harmoniapp.entities.profile.Role;
import org.harmoniapp.entities.schedule.PredefineShift;
import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.geneticalgorithm.*;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.configuration.Principle;
import org.harmoniapp.contracts.notification.NotificationDto;
import org.harmoniapp.exception.NotEnoughEmployees;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Service for AI schedule generation.
 * Provides methods for generating schedules based on requirements using a genetic algorithm.
 */
@Service
@RequiredArgsConstructor
public class AiScheduleService {
    private final RepositoryCollector repositoryCollector;
    private final SimpMessagingTemplate messagingTemplate;
    private List<Long> lastGeneratedShiftIds;

    /**
     * Generates a schedule based on the specified requirements.
     *
     * @param requirementsDto the list of schedule requirements to generate the schedule from
     * @param authentication  the authentication object containing the user's credentials
     * @return an AiSchedulerResponse containing the generated schedule
     */
    public AiSchedulerResponse generateSchedule(List<ScheduleRequirement> requirementsDto, Authentication authentication) {
        requirementsDto.sort(Comparator.comparing(ScheduleRequirement::date));

        List<User> users = repositoryCollector.getUsers().findAllActiveWithoutAbsenceInDateRange(
                requirementsDto.getFirst().date(), requirementsDto.getLast().date());
        List<PredefineShift> predefineShifts = repositoryCollector.getPredefineShifts().findAll();
        List<Role> roles = repositoryCollector.getRoles().findAll();

        Map<String, List<Employee>> employees = prepareEmployees(requirementsDto, users);
        verifyUserQuantity(requirementsDto, employees, roles);

        List<Gen> shifts = prepareShifts(requirementsDto, predefineShifts, roles);

        Principle principle = (Principle) authentication.getPrincipal();
        User receiver = repositoryCollector.getUsers().findById(principle.id()).orElseThrow();
        GenerationListener listener = new AiGenerationListener(messagingTemplate, receiver.getId());
        Algorithm geneticAlgorithm = new GeneticAlgorithm(1000, listener);
        Chromosome chromosome = geneticAlgorithm.run(shifts, employees);

        if (chromosome.getFitness() < 0.9) {
            createAndSendFailedNotification(receiver);
            return new AiSchedulerResponse(
                    "Nie udało się wygenerować grafiku, spróbuj ponownie", false
            );
        }

        List<Shift> decodedShifts = decodeShifts(chromosome.getGens(), users, predefineShifts, roles);
        lastGeneratedShiftIds = repositoryCollector.getShifts().saveAll(decodedShifts).stream()
                .map(Shift::getId)
                .toList();

        createAndSendSuccessfulNotification(receiver);
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
            Set<Role> userRoles = user.getRoles();
            for (Role role : userRoles) {
                if (validRoles.contains(role.getId())) {
                    Employee employee = new Employee(user.getEmployeeId(), role.getName());
                    employees.add(employee);
                    break;
                }
            }
        }
        return employees.stream().collect(Collectors.groupingBy(Employee::role));
    }

    /**
     * Prepares shifts for the schedule generation.
     *
     * @param scheduleRequirements the list of schedule requirements
     * @return a list of shifts
     */
    private List<Gen> prepareShifts(List<ScheduleRequirement> scheduleRequirements, List<PredefineShift> predefineShifts,
                                    List<Role> roles) {
        List<Gen> shifts = new ArrayList<>();

        for (ScheduleRequirement scheduleRequirement : scheduleRequirements) {

            scheduleRequirement.shifts()
                    .sort(Comparator.comparing(rs -> predefineShifts.stream()
                            .filter(ps -> ps.getId().equals(rs.shiftId()))
                            .findFirst()
                            .orElseThrow()
                            .getStart())
                    );
            for (ReqShiftDto reqShiftDto : scheduleRequirement.shifts()) {
                List<Requirements> requirements = prepareRequirements(reqShiftDto.roles(), roles);
                shifts.add(new Gen(reqShiftDto.shiftId().intValue(),
                        scheduleRequirement.date().getDayOfYear(),
                        predefineShifts.stream()
                                .filter(ps -> ps.getId().equals(reqShiftDto.shiftId()))
                                .findFirst()
                                .orElseThrow()
                                .getStart(),
                        null,
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
    private List<Shift> decodeShifts(List<Gen> shifts, List<User> users,
                                                                         List<PredefineShift> predefineShifts, List<Role> roles) {
        List<Shift> decodedShiftList = new ArrayList<>(shifts.size());
        LocalDate now = LocalDate.now();

        for (Gen shift : shifts) {
            PredefineShift predShift = predefineShifts.stream()
                    .filter(ps -> ps.getId().equals((long) shift.id()))
                    .findFirst()
                    .orElseThrow();
            LocalDate date = LocalDate.ofYearDay(
                    (now.getDayOfYear() <= shift.day()) ? now.getYear() : now.getYear() + 1, shift.day());
            LocalDateTime start = LocalDateTime.of(date, predShift.getStart());
            LocalDateTime end = LocalDateTime.of(
                    (predShift.getStart().isBefore(predShift.getEnd())) ? date : date.plusDays(1), predShift.getEnd()
            );

            for (Employee employee : shift.employees()) {
                Shift decodedShift = new Shift();
                decodedShift.setStart(start);
                decodedShift.setEnd(end);
                decodedShift.setUser(users.stream()
                        .filter(u -> u.getEmployeeId().equals(employee.id()))
                        .findFirst()
                        .orElseThrow());
                decodedShift.setRole(roles.stream()
                        .filter(r -> r.getName().equals(employee.role()))
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
        Map<String, Integer> available = calculateAvailableEmployees(requirementsDto, employees);
        checkEmployeeAvailability(required, available);
    }

    /**
     * Calculates the available employees for the schedule generation.
     *
     * @param requirementsDto the list of schedule requirements
     * @param employees       the map of employees grouped by role
     * @return a map of roles and available employees
     */
    private Map<String, Integer> calculateAvailableEmployees(List<ScheduleRequirement> requirementsDto, Map<String, List<Employee>> employees) {
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
     * @throws NotEnoughEmployees if there are not enough employees available to meet the requirements
     */
    private void checkEmployeeAvailability(Map<String, Integer> required, Map<String, Integer> available) throws NotEnoughEmployees {
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

    /**
     * Revokes the last generated schedule.
     * Removes the last generated shifts if they are not published.
     *
     * @return an AiSchedulerResponse containing the result of the revocation
     */
    @Transactional
    public AiSchedulerResponse revokeSchedule() {
        if (lastGeneratedShiftIds == null || lastGeneratedShiftIds.isEmpty()) {
            return new AiSchedulerResponse(
                    "Nie ma żadnego grafiku do usunięcia", null
            );
        }

        List<Shift> shifts = repositoryCollector.getShifts().findAllById(lastGeneratedShiftIds);
        shifts.removeIf(Shift::getPublished);
        repositoryCollector.getShifts().deleteAll(shifts);
        lastGeneratedShiftIds = null;

        return new AiSchedulerResponse(
                "Usunięto ostatnio wygenerowany grafik", null
        );
    }

    /**
     * Creates and sends a notification indicating that the automatic schedule generation was successful.
     *
     * @param user the user to whom the notification will be sent
     */
    private void createAndSendSuccessfulNotification(User user) {
        NotificationType type = repositoryCollector.getNotificationTypes().findById(8L).orElseThrow();
        Notification notification = Notification.builder()
                .user(user)
                .title("Automatyczne układanie grafiku ukończone")
                .message("Grafik został pomyślnie wygenerowany, zobacz teraz w kalendarzu.")
                .type(type)
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();
        notification = repositoryCollector.getNotifications().save(notification);
        messagingTemplate.convertAndSend("/client/notifications/" + user.getId(),
                NotificationDto.fromEntity(notification));
    }

    /**
     * Creates and sends a notification indicating that the automatic schedule generation failed.
     *
     * @param user the user to whom the notification will be sent
     */
    private void createAndSendFailedNotification(User user) {
        NotificationType type = repositoryCollector.getNotificationTypes().findById(7L).orElseThrow();
        Notification notification = Notification.builder()
                .user(user)
                .title("Automatyczne układanie grafiku nie powiodło się")
                .message("Nie udało się wygenerować grafiku, spróbuj ponownie.")
                .type(type)
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();
        notification = repositoryCollector.getNotifications().save(notification);
        messagingTemplate.convertAndSend("/client/notifications/" + user.getId(),
                NotificationDto.fromEntity(notification));
    }

    /**
     * Listener for generation updates in the genetic algorithm.
     * Sends progress updates to the client via WebSocket.
     */
    private record AiGenerationListener(SimpMessagingTemplate messagingTemplate,
                                        long receiverId) implements GenerationListener {
        @Override
        public void onGenerationUpdate(int generation, double fitness) {
            GeneratingProgressDto response = new GeneratingProgressDto(generation, fitness);
            messagingTemplate.convertAndSend("/client/fitness/" + receiverId, response);
        }
    }
}
