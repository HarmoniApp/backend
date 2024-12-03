package org.harmoniapp.harmoniwebapi.geneticalgorithm;

import lombok.Data;

import java.util.List;

/**
 * Represents a chromosome in the genetic algorithm.
 */
@Data
public class Chromosome {
    private List<Gen> gens;
    private double fitness;

    /**
     * Constructs a Chromosome with the given genes and evaluates its fitness.
     *
     * @param gens the list of genes
     * @param checker the constraint checker to evaluate fitness
     */
    public Chromosome(List<Gen> gens, CheckConstraint checker) {
        this.gens = gens;
        evaluateFitness(checker);
    }

    /**
     * Evaluates the fitness of the chromosome based on constraint violations.
     *
     * @param checker the constraint checker to evaluate fitness
     */
    public void evaluateFitness(CheckConstraint checker) {
        double violations = checker.checkViolations(this.gens);
        this.fitness = 1 / (1 + violations);
    }
}
