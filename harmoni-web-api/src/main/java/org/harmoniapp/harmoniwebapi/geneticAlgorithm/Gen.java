package org.harmoniapp.harmoniwebapi.geneticAlgorithm;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a shift in the genetic algorithm.
 */
@Data
@AllArgsConstructor
public class Gen {
    private final int id;
    private final int day;
    private final LocalTime startTime;
    private List<Employee> employees;
    private final List<Requirements> requirements;

    /**
     * Creates a new Shift instance.
     *
     * @param id           the ID of the shift
     * @param day          the day of the shift
     * @param requirements the requirements for the shift
     */
    public Gen(int id, int day, LocalTime time, List<Requirements> requirements) {
        this.id = id;
        this.day = day;
        this.startTime = time;
        this.employees = new ArrayList<>();
        this.requirements = requirements;
    }
}
