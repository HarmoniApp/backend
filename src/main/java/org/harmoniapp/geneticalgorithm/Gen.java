package org.harmoniapp.geneticalgorithm;

import java.time.LocalTime;
import java.util.List;

/**
 * Represents a shift in the genetic algorithm.
 *
 * @param id           the unique identifier of the shift
 * @param day          the day of the shift
 * @param startTime    the start time of the shift
 * @param employees    the list of employees assigned to the shift
 * @param requirements the list of requirements for the shift
 */
public record Gen(int id, int day, LocalTime startTime, List<Employee> employees, List<Requirements> requirements) {
}
