package org.harmoniapp.services.schedule.aischedule;

import org.harmoniapp.contracts.schedule.aischedule.AggregatedScheduleData;
import org.harmoniapp.entities.schedule.Shift;
import org.harmoniapp.geneticalgorithm.Gen;

import java.util.List;

/**
 * Interface for mapping algorithm entities.
 */
public interface AlgorithmEntityMapper {

    /**
     * Decodes a list of genetic algorithm shifts into a list of Shift entities.
     *
     * @param shifts the list of genetic algorithm shifts
     * @param data   the aggregated schedule data
     * @return the list of decoded Shift entities
     */
    List<Shift> decodeShifts(List<Gen> shifts, AggregatedScheduleData data);
}
