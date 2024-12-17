package org.harmoniapp.services.schedule;

import org.harmoniapp.contracts.schedule.ScheduleRequestDto;
import org.harmoniapp.contracts.schedule.UserScheduleDto;

/**
 * Service interface for managing user schedules.
 */
public interface ScheduleService {

    /**
     * Retrieves the weekly schedule for a specific user.
     *
     * @param userId             the ID of the user
     * @param scheduleRequestDto the schedule request details
     * @return the user's weekly schedule
     */
    UserScheduleDto getUserWeeklySchedule(Long userId, ScheduleRequestDto scheduleRequestDto);
}
