package org.harmoniapp.geneticalgorithm;

import java.util.List;
import java.util.Map;

/**
 * Interface representing a genetic algorithm.
 */
public interface Algorithm {

    /**
     * Runs the genetic algorithm.
     *
     * @param shifts    the list of genetic shifts
     * @param employees the map of employees categorized by some criteria
     * @return the resulting Chromosome after running the algorithm
     */
    Chromosome run(List<Gen> shifts, Map<String, List<Employee>> employees);
}
