package org.harmoniapp.services.schedule.aischedule;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.configuration.Principle;
import org.harmoniapp.contracts.notification.NotificationDto;
import org.harmoniapp.contracts.schedule.aischedule.AggregatedScheduleData;
import org.harmoniapp.contracts.schedule.aischedule.AiSchedulerResponseDto;
import org.harmoniapp.contracts.schedule.aischedule.ScheduleRequirement;
import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.enums.AiSchedulerNotificationType;
import org.harmoniapp.geneticalgorithm.Algorithm;
import org.harmoniapp.geneticalgorithm.Chromosome;
import org.harmoniapp.geneticalgorithm.Gen;
import org.harmoniapp.geneticalgorithm.GeneticAlgorithm;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.services.notification.NotificationService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the AiScheduleService interface.
 * Provides methods for generating and managing AI-based schedules.
 */
@Service
@RequiredArgsConstructor
public class AiScheduleServiceImpl implements AiScheduleService {
    private final RepositoryCollector repositoryCollector;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ScheduleDataEncoder requirementsEncoder;
    private final AlgorithmEntityMapper algorithmEntityMapper;
    private List<Long> lastGeneratedShiftIds;

    /**
     * Generates a schedule based on the provided requirements.
     *
     * @param requirementsDto the list of schedule requirements
     * @param authentication  the authentication information of the user
     * @return the generated schedule response
     */
    public AiSchedulerResponseDto generateSchedule(List<ScheduleRequirement> requirementsDto, Authentication authentication) {
        AggregatedScheduleData data = requirementsEncoder.prepareData(requirementsDto);
        User receiver = getReceiver(authentication);

        List<Gen> gens;
        try {
            gens = runAlgorithm(data, receiver);
        } catch (RuntimeException e) {
            return failedResponse(receiver);
        }
        List<Shift> decodedShifts = algorithmEntityMapper.decodeShifts(gens, data);
        lastGeneratedShiftIds = saveShifts(decodedShifts);

        return successfulResponse(receiver);
    }

    /**
     * Retrieves the user associated with the given authentication object.
     *
     * @param authentication the authentication object containing the user's credentials
     * @return the user associated with the given authentication object
     */
    private User getReceiver(Authentication authentication) {
        Principle principle = (Principle) authentication.getPrincipal();
        return getUserById(principle.id());
    }

    /**
     * Retrieves a user by their ID if they are active.
     *
     * @param id the ID of the user to retrieve
     * @return the user with the specified ID if they are active
     * @throws IllegalArgumentException if the user is not found
     */
    private User getUserById(long id) {
        return repositoryCollector.getUsers().findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika"));
    }

    /**
     * Runs the genetic algorithm to generate a schedule.
     *
     * @param data     the aggregated schedule data containing shifts and employees
     * @param receiver the user for whom the schedule is being generated
     * @return a list of genes representing the generated schedule
     * @throws RuntimeException if the generated schedule's fitness is below the acceptable threshold
     */
    private List<Gen> runAlgorithm(AggregatedScheduleData data, User receiver) {
        Algorithm geneticAlgorithm = new GeneticAlgorithm(1000);
        geneticAlgorithm.addObserver(new WsGenerationObserver(messagingTemplate, receiver.getId()));
        geneticAlgorithm.addObserver(new LogGenerationObserver()); // Observer for logging
        Chromosome chromosome = geneticAlgorithm.run(data.shifts(), data.employees());

        if (chromosome.getFitness() < 0.9) {
            throw new RuntimeException("Nie udało się wygenerować grafiku");
        }
        return chromosome.getGens();
    }

    /**
     * Creates a response indicating that the schedule generation failed.
     * Sends a notification to the user about the failure.
     *
     * @param receiver the user to whom the notification will be sent
     * @return a response containing the failure message and status
     */
    private AiSchedulerResponseDto failedResponse(User receiver) {
        sendNotification(receiver, AiSchedulerNotificationType.FAILURE);
        return new AiSchedulerResponseDto("Nie udało się wygenerować grafiku, spróbuj ponownie", false);
    }

    /**
     * Creates a response indicating that the schedule generation was successful.
     * Sends a notification to the user about the success.
     *
     * @param receiver the user to whom the notification will be sent
     * @return a ResponseEntity containing the success message and HTTP status
     */
    private AiSchedulerResponseDto successfulResponse(User receiver) {
        sendNotification(receiver, AiSchedulerNotificationType.SUCCESS);
        return new AiSchedulerResponseDto("Układanie grafiku zakończone pomyślnie", true);
    }

    /**
     * Sends a notification to the specified user.
     *
     * @param user the user to whom the notification will be sent
     * @param type the type of notification to be sent
     */
    private void sendNotification(User user, AiSchedulerNotificationType type) {
        NotificationDto notification = NotificationDto.createNotification(user.getId(), type.getTitle(), type.getMessage());
        notification = notificationService.create(notification);
        messagingTemplate.convertAndSend("/client/notifications/" + user.getId(), notification);
    }

    /**
     * Saves the provided list of decoded shifts to the database.
     *
     * @param decodedShifts the list of decoded shifts to be saved
     * @return a list of IDs of the saved shifts
     */
    private List<Long> saveShifts(List<Shift> decodedShifts) {
        return repositoryCollector.getShifts().saveAll(decodedShifts).stream()
                .map(Shift::getId)
                .toList();
    }

    /**
     * Revokes the last generated schedule.
     * Removes the last generated shifts if they are not published.
     *
     * @return an AiSchedulerResponse containing the result of the revocation
     */
    @Transactional
    public AiSchedulerResponseDto revokeSchedule() {
        if (isLastGeneratedShiftIdsEmpty()) {
            return new AiSchedulerResponseDto(
                    "Nie ma żadnego grafiku do usunięcia", null
            );
        }

        List<Shift> shifts = repositoryCollector.getShifts().findAllById(lastGeneratedShiftIds);
        removeUnpublishedShifts(shifts);
        lastGeneratedShiftIds = null;

        return new AiSchedulerResponseDto("Usunięto ostatnio wygenerowany grafik", null);
    }

    /**
     * Checks if the list of last generated shift IDs is empty.
     *
     * @return true if the list is null or empty, false otherwise
     */
    private boolean isLastGeneratedShiftIdsEmpty() {
        return lastGeneratedShiftIds == null || lastGeneratedShiftIds.isEmpty();
    }

    /**
     * Removes unpublished shifts from the provided list.
     *
     * @param shifts the list of shifts to be filtered and removed if unpublished
     */
    private void removeUnpublishedShifts(List<Shift> shifts) {
        shifts.removeIf(Shift::getPublished);
        repositoryCollector.getShifts().deleteAll(shifts);
    }
}
