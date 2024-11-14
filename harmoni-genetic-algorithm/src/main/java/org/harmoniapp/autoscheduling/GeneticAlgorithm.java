package org.harmoniapp.autoscheduling;

import java.util.*;

public class GeneticAlgorithm {
    private final int populationSize;
    private final int tournamentSize;
    private final int maxGenerations;
    private final double mutationRate;
    private final double crossoverRate;
    private final ConstraintChecker constraintChecker;
    private final Random random;

    public GeneticAlgorithm() {
        this.populationSize = 50;
        this.tournamentSize = populationSize / 5;
        this.maxGenerations = 20000;
        this.mutationRate = 0.02;
        this.crossoverRate = 0.6;
        this.constraintChecker = new ConstraintChecker();
        this.random = new Random();
    }

    public GeneticAlgorithm(int populationSize, int maxGenerations, double mutationRate, double crossoverRate) {
        this.populationSize = populationSize;
        this.tournamentSize = populationSize / 5;
        this.maxGenerations = maxGenerations;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.constraintChecker = new ConstraintChecker();
        this.random = new Random();
    }

    public List<Shift> run(List<Shift> shifts, Map<String, List<Employee>> employees) {
        List<Chromosome> population = initializePopulation(shifts, employees);

        assert !population.isEmpty();
        Chromosome bestChromosome = population.stream().max(Comparator.comparing(Chromosome::getFitness)).get();

        for (int i = 0; i < maxGenerations; i++) {
            population = evolvePopulation(population, employees);

            assert !population.isEmpty();
            Chromosome newBestChromosome = population.stream().max(Comparator.comparing(Chromosome::getFitness)).get();
            if (newBestChromosome.getFitness() > bestChromosome.getFitness()) {
                bestChromosome = newBestChromosome;
            }

            if (i % 100 == 0) {
                System.out.println("Generation: " + i + " Fitness: " + bestChromosome.getFitness());
            }

            if (bestChromosome.getFitness() == 1) {
                System.out.println("Generation: " + i + " Fitness: " + bestChromosome.getFitness());
                break;
            }
        }

        return bestChromosome.getGens();
    }

    private List<Chromosome> initializePopulation(List<Shift> shifts, Map<String, List<Employee>> employees) {
        List<Chromosome> population = new ArrayList<>(shifts.size());
        for (int i = 0; i < populationSize; i++) {
            population.add(generateRandomChromosome(shifts, employees));
        }
        return population;
    }

    private Chromosome generateRandomChromosome(List<Shift> shifts, Map<String, List<Employee>> employees) {
        List<Shift> gens = new ArrayList<>(shifts.size());
        for (Shift shift : shifts) {
            List<Employee> employeesForShift = selectRandomEmployees(shift.getRequirements(), employees);
            gens.add(new Shift(shift.getId(), shift.getDay(), employeesForShift, shift.getRequirements()));
        }
        return new Chromosome(gens, constraintChecker);
    }

    private List<Chromosome> evolvePopulation(List<Chromosome> population, Map<String, List<Employee>> employeesByRole) {
        assert !population.isEmpty();
        Chromosome best = population.stream().max(Comparator.comparing(Chromosome::getFitness)).get();

        List<Chromosome> newPopulation = new ArrayList<>(population.size());
        newPopulation.add(best);
        newPopulation.add(best);
        for (int i = 0; i < populationSize / 2 - 1; i++) {
            Chromosome parent1 = tournamentSelection(population);
            Chromosome parent2 = tournamentSelection(population);
            List<Chromosome> offspring = crossover(parent1, parent2);
            for (Chromosome child : offspring) {
                child = mutate(child, employeesByRole);
                child.evaluateFitness(constraintChecker);
                newPopulation.add(child);
            }
        }
        return newPopulation;
    }

    private Chromosome tournamentSelection(List<Chromosome> population) {
        List<Chromosome> tournament = new ArrayList<>(tournamentSize);
        for (int i = 0; i < tournamentSize; i++) {
            tournament.add(population.get(random.nextInt(population.size())));
        }
        assert !tournament.isEmpty();
        return tournament.stream().max(Comparator.comparing(Chromosome::getFitness)).get();
    }

    private List<Chromosome> crossover(Chromosome parent1, Chromosome parent2) {
        if (random.nextDouble() > crossoverRate) {
            return Arrays.asList(parent1, parent2);
        }

        List<Shift> gens1 = parent1.getGens();
        List<Shift> gens2 = parent2.getGens();
        List<Shift> childGens1 = new ArrayList<>(gens1.size());
        List<Shift> childGens2 = new ArrayList<>(gens1.size());
        for (int i = 0; i < gens1.size(); i++) {
            if (random.nextDouble() < 0.5) {
                childGens1.add(gens2.get(i));
                childGens2.add(gens1.get(i));
            } else {
                childGens1.add(gens1.get(i));
                childGens2.add(gens2.get(i));
            }
        }
        return Arrays.asList(new Chromosome(childGens1, constraintChecker), new Chromosome(childGens2, constraintChecker));
    }

    private Chromosome mutate(Chromosome chromosome, Map<String, List<Employee>> employees) {
        List<Shift> gens = chromosome.getGens();
        for (int i = 0; i < gens.size(); i++) {
            if (random.nextDouble() > mutationRate) continue;

            List<Employee> employeesForShift = selectRandomEmployees(gens.get(i).getRequirements(), employees);
            gens.set(i, new Shift(gens.get(i).getId(), gens.get(i).getDay(), employeesForShift, gens.get(i).getRequirements()));

        }
        return new Chromosome(gens, constraintChecker);
    }

    private List<Employee> selectRandomEmployees(List<Requirements> requirements, Map<String, List<Employee>> employees) {
        List<Employee> employeesForShift = new ArrayList<>();
        for (Requirements req : requirements) {
            List<Employee> copy = new ArrayList<>(employees.get(req.getRole()));
            for (int j = 0; j < req.getEmployeesNumber(); j++) {
                int randomIndex = random.nextInt(copy.size());
                employeesForShift.add(copy.get(randomIndex));
                copy.remove(randomIndex);
            }
        }
        return employeesForShift;
    }
}