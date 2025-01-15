package org.harmoniapp.geneticalgorithm;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a chromosome in the genetic algorithm.
 */
@Data
@AllArgsConstructor
public class Chromosome {
    private List<Gen> gens;
    private CheckConstraint checker;
    private double fitness;

    /**
     * Constructs a Chromosome with the given genes and evaluates its fitness.
     *
     * @param gens the list of genes
     */
    public Chromosome(List<Gen> gens) {
        this.gens = gens;
        this.checker = ConstraintChecker.getInstance();
        evaluateFitness();
    }

    /**
     * Returns a copy of the list of genes.
     *
     * @return a new list containing the genes
     */
    public List<Gen> getGens() {
        return new ArrayList<>(gens);
    }

    /**
     * Evaluates the fitness of the chromosome based on constraint violations.
     */
    public void evaluateFitness() {
        double violations = checker.checkViolations(this.gens);
        this.fitness = 1 / (1 + violations);
    }
}
