package org.harmoniapp.harmoniwebapi.geneticalgorithm;

import lombok.AllArgsConstructor;

import java.util.*;

/**
 * Represents the genetic algorithm used to generate schedules.
 */
@AllArgsConstructor
public class GeneticAlgorithm implements Algorithm {
    private final int populationSize;
    private final int tournamentSize;
    private final int maxGenerations;
    private final double mutationRate;
    private final double crossoverRate;
    private final CheckConstraint constraintChecker;
    private final Random random;
    private final int reportInterval;
    private final GenerationListener listener;

    /**
     * Creates a new GeneticAlgorithm instance with default parameters.
     */
    public GeneticAlgorithm() {
        this.populationSize = 50;
        this.tournamentSize = 10;
        this.maxGenerations = 100000;
        this.mutationRate = 0.02;
        this.crossoverRate = 0.6;
        this.constraintChecker = new ConstraintChecker();
        this.random = new Random();
        this.reportInterval = 100;
        this.listener = null;
    }

    /**
     * Creates a new GeneticAlgorithm instance with specified parameters.
     *
     * @param reportInterval the interval at which progress is reported
     * @param listener       the listener for generation updates
     */
    public GeneticAlgorithm(int reportInterval, GenerationListener listener) {
        this.populationSize = 50;
        this.tournamentSize = 10;
        this.maxGenerations = 100000;
        this.mutationRate = 0.02;
        this.crossoverRate = 0.6;
        this.constraintChecker = new ConstraintChecker();
        this.random = new Random();
        this.reportInterval = reportInterval;
        this.listener = listener;
    }

    /**
     * Runs the genetic algorithm to generate a schedule.
     *
     * @param shifts    the list of shifts to generate the schedule from
     * @param employees the list of employees to generate the schedule from
     * @return the generated schedule
     */
    @Override
    public Chromosome run(List<Gen> shifts, Map<String, List<Employee>> employees) {
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

            if ((i % reportInterval == 0 || bestChromosome.getFitness() == 1) && listener != null) {
                listener.onGenerationUpdate(i / this.maxGenerations, bestChromosome.getFitness());
            }

            if (bestChromosome.getFitness() == 1) {
                break;
            }
        }

        return bestChromosome;
    }

    /**
     * Initializes the population of chromosomes.
     *
     * @param shifts    the list of shifts to generate the schedule from
     * @param employees the list of employees to generate the schedule from
     * @return the initialized population
     */
    private List<Chromosome> initializePopulation(List<Gen> shifts, Map<String, List<Employee>> employees) {
        List<Chromosome> population = new ArrayList<>(shifts.size());
        for (int i = 0; i < populationSize; i++) {
            population.add(generateRandomChromosome(shifts, employees));
        }
        return population;
    }

    /**
     * Generates a random chromosome.
     *
     * @param shifts    the list of shifts to generate the chromosome from
     * @param employees the list of employees to generate the chromosome from
     * @return the generated chromosome
     */
    private Chromosome generateRandomChromosome(List<Gen> shifts, Map<String, List<Employee>> employees) {
        List<Gen> gens = new ArrayList<>(shifts.size());
        for (Gen shift : shifts) {
            List<Employee> employeesForShift = selectRandomEmployees(shift.requirements(), employees);
            gens.add(new Gen(shift.id(), shift.day(), shift.startTime(), employeesForShift, shift.requirements()));
        }
        return new Chromosome(gens, constraintChecker);
    }

    /**
     * Evolves the population of chromosomes.
     *
     * @param population      the population to evolve
     * @param employeesByRole the list of employees grouped by role
     * @return the evolved population
     */
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

    /**
     * Selects a chromosome using tournament selection.
     *
     * @param population the population to select from
     * @return the selected chromosome
     */
    private Chromosome tournamentSelection(List<Chromosome> population) {
        List<Chromosome> tournament = new ArrayList<>(tournamentSize);
        for (int i = 0; i < tournamentSize; i++) {
            tournament.add(population.get(random.nextInt(population.size())));
        }
        assert !tournament.isEmpty();
        return tournament.stream().max(Comparator.comparing(Chromosome::getFitness)).get();
    }

    /**
     * Performs crossover on two chromosomes.
     *
     * @param parent1 the first parent chromosome
     * @param parent2 the second parent chromosome
     * @return the offspring chromosomes
     */
    private List<Chromosome> crossover(Chromosome parent1, Chromosome parent2) {
        if (random.nextDouble() > crossoverRate) {
            return Arrays.asList(parent1, parent2);
        }

        List<Gen> gens1 = parent1.getGens();
        List<Gen> gens2 = parent2.getGens();
        List<Gen> childGens1 = new ArrayList<>(gens1.size());
        List<Gen> childGens2 = new ArrayList<>(gens1.size());
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

    /**
     * Mutates a chromosome.
     *
     * @param chromosome the chromosome to mutate
     * @param employees  the list of employees to mutate the chromosome from
     * @return the mutated chromosome
     */
    private Chromosome mutate(Chromosome chromosome, Map<String, List<Employee>> employees) {
        List<Gen> gens = chromosome.getGens();
        for (int i = 0; i < gens.size(); i++) {
            if (random.nextDouble() > mutationRate) continue;

            List<Employee> employeesForShift = selectRandomEmployees(gens.get(i).requirements(), employees);
            gens.set(i, new Gen(gens.get(i).id(), gens.get(i).day(), gens.get(i).startTime(), employeesForShift, gens.get(i).requirements()));

        }
        return new Chromosome(gens, constraintChecker);
    }

    /**
     * Selects random employees for a shift.
     *
     * @param requirements the requirements for the shift
     * @param employees    the list of employees to select from
     * @return the selected employees
     */
    private List<Employee> selectRandomEmployees(List<Requirements> requirements, Map<String, List<Employee>> employees) {
        List<Employee> employeesForShift = new ArrayList<>();
        for (Requirements req : requirements) {
            if (!employees.containsKey(req.role())) {
                throw new IllegalArgumentException("No employees with role " + req.role());
            }
            if (employees.get(req.role()).size() < req.employeesNumber()) {
                throw new IllegalArgumentException("Not enough employees with role " + req.role());
            }
            List<Employee> copy = new ArrayList<>(employees.get(req.role()));
            for (int j = 0; j < req.employeesNumber(); j++) {
                int randomIndex = random.nextInt(copy.size());
                employeesForShift.add(copy.get(randomIndex));
                copy.remove(randomIndex);
            }
        }
        return employeesForShift;
    }
}