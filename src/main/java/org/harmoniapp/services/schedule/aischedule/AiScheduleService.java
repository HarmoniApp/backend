package org.harmoniapp.services.schedule.aischedule;

import org.harmoniapp.contracts.schedule.aischedule.AiSchedulerResponseDto;
import org.harmoniapp.contracts.schedule.aischedule.ScheduleRequirement;
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
     * @param authentication  the authentication information of the user
     * @return the generated schedule response
     */
    AiSchedulerResponseDto generateSchedule(List<ScheduleRequirement> requirementsDto, Authentication authentication);

    /**
     * Revokes the current schedule.
     *
     * @return the response after revoking the schedule
     */
    AiSchedulerResponseDto revokeSchedule();
}
