package org.harmoniapp.harmoniwebapi.geneticAlgorithm;

import lombok.Data;

import java.util.List;

/**
 * Represents a chromosome in the genetic algorithm.
 */
@Data
public class Chromosome {
    private List<Gen> gens;
    private double fitness;

    public Chromosome(List<Gen> gens, ConstraintChecker checker) {
        this.gens = gens;
        evaluateFitness(checker);
    }

    public void evaluateFitness(ConstraintChecker checker) {
        double violations = checker.checkViolations(this.gens);
        this.fitness = 1 / (1 + violations);
    }
}
