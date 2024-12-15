package org.harmoniapp.services.schedule.aischedule;

import org.harmoniapp.contracts.schedule.aischedule.AggregatedScheduleData;
import org.harmoniapp.contracts.schedule.aischedule.ScheduleRequirement;

import java.util.List;

/**
 * Interface for encoding schedule data.
 */
public interface ScheduleDataEncoder {

    /**
     * Prepares collected data from a list of schedule requirements.
     *
     * @param requirementsDto the list of schedule requirements
     * @return the collected data
     */
    AggregatedScheduleData prepareData(List<ScheduleRequirement> requirementsDto);
}
