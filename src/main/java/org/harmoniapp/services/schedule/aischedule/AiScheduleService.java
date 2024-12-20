package org.harmoniapp.services.schedule.aischedule;

import org.harmoniapp.contracts.schedule.aischedule.AiSchedulerResponse;
import org.harmoniapp.contracts.schedule.aischedule.ScheduleRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * Service interface for AI-based schedule generation and revocation.
 */
public interface AiScheduleService {

    /**
     * Generates a schedule based on the provided requirements.
     *
     * @param requirementsDto the list of schedule requirements
     * @param authentication the authentication information of the user
     * @return the response entity containing the AI scheduler response
     */
    ResponseEntity<AiSchedulerResponse> generateSchedule(List<ScheduleRequirement> requirementsDto, Authentication authentication);

    /**
     * Revokes the current schedule.
     *
     * @return the response after revoking the schedule
     */
    AiSchedulerResponse revokeSchedule();
}
