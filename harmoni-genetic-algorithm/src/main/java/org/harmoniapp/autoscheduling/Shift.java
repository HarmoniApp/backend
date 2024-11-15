package org.harmoniapp.autoscheduling;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a shift in the genetic algorithm.
 */
@Data
@AllArgsConstructor
public class Shift {
    private final int id;
    private final int day;
    private List<Employee> employees;
    private final List<Requirements> requirements;

    /**
     * Creates a new Shift instance.
     *
     * @param id           the ID of the shift
     * @param day          the day of the shift
     * @param requirements the requirements for the shift
     */
    public Shift(int id, int day, List<Requirements> requirements) {
        this.id = id;
        this.day = day;
        this.employees = new ArrayList<>();
        this.requirements = requirements;
    }
}
