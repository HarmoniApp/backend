package org.harmoniapp.geneticalgorithm;

import java.util.List;

/**
 * Interface for checking constraints on a chromosome.
 */
public interface CheckConstraint {

    /**
     * Checks for violations in the given chromosome.
     *
     * @param chromosome the list of genes representing the chromosome
     * @return the number of violations as a double
     */
    double checkViolations(List<Gen> chromosome);
}
