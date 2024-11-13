package org.harmoniapp.autoscheduling;

import lombok.Data;

import java.util.List;

@Data
public class Chromosome {
    private List<Shift> gens;
    private double fitness;

    public Chromosome(List<Shift> gens, ConstraintChecker checker) {
        this.gens = gens;
        evaluateFitness(checker);
    }

    public void evaluateFitness(ConstraintChecker checker) {
        double violations = checker.checkViolations(this.gens);
        this.fitness = 1 / (1 + violations);
    }
}
