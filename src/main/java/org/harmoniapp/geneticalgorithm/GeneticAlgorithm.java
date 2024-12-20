package org.harmoniapp.geneticalgorithm;

import lombok.AllArgsConstructor;

import java.util.*;
import java.util.stream.IntStream;

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
    private List<GenerationObserver> observers;

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
        this.observers = new ArrayList<>();
        addObserver(new DefaultGenerationObserver());
    }

    /**
     * Creates a new GeneticAlgorithm instance with specified parameters.
     *
     * @param reportInterval the interval at which progress is reported
     */
    public GeneticAlgorithm(int reportInterval) {
        this.populationSize = 50;
        this.tournamentSize = 10;
        this.maxGenerations = 100000;
        this.mutationRate = 0.02;
        this.crossoverRate = 0.6;
        this.constraintChecker = new ConstraintChecker();
        this.random = new Random();
        this.reportInterval = reportInterval;
        this.observers = new ArrayList<>();
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
        Chromosome bestChromosome = getBestChromosome(population);

        for (int i = 0; i < maxGenerations; i++) {
            population = evolvePopulation(population, employees);
            bestChromosome = updateBestChromosome(population, bestChromosome);

            notifyObservers(i, bestChromosome);
            if (bestChromosome.getFitness() == 1) {
                break;
            }
        }

        return bestChromosome;
    }

    /**
     * Adds an observer to the genetic algorithm.
     *
     * @param observer the observer to add
     */
    @Override
    public void addObserver(GenerationObserver observer) {
        this.observers.add(observer);
    }

    /**
     * Notifies all observers of a generation update.
     *
     * @param generation     the generation number
     * @param bestChromosome the chromosome of the current generation
     */
    public void notifyObservers(int generation, Chromosome bestChromosome) {
        if ((generation % reportInterval == 0 || bestChromosome.getFitness() == 1)) {
            double progress = (double) generation / this.maxGenerations * 100;
            observers.forEach(observer -> observer.onGenerationUpdate(progress, bestChromosome.getFitness()));
        }
    }

    /**
     * Retrieves the best chromosome from the given population based on fitness.
     *
     * @param population the list of chromosomes to evaluate
     * @return the chromosome with the highest fitness
     * @throws NoSuchElementException if the population is empty
     */
    private Chromosome getBestChromosome(List<Chromosome> population) {
        assert !population.isEmpty();
        return population.stream().max(Comparator.comparing(Chromosome::getFitness)).get();
    }

    /**
     * Updates the best chromosome found so far.
     *
     * @param population     the current population of chromosomes
     * @param bestChromosome the best chromosome found so far
     * @return the updated best chromosome
     */
    private Chromosome updateBestChromosome(List<Chromosome> population, Chromosome bestChromosome) {
        Chromosome newBestChromosome = getBestChromosome(population);
        if (newBestChromosome.getFitness() > bestChromosome.getFitness()) {
            bestChromosome = newBestChromosome;
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
        return IntStream.range(0, populationSize)
                .mapToObj(i -> generateRandomChromosome(shifts, employees))
                .toList();
    }

    /**
     * Generates a random chromosome.
     *
     * @param shifts    the list of shifts to generate the chromosome from
     * @param employees the list of employees to generate the chromosome from
     * @return the generated chromosome
     */
    private Chromosome generateRandomChromosome(List<Gen> shifts, Map<String, List<Employee>> employees) {
        List<Gen> gens = shifts.stream()
                .map(shift -> new Gen(
                        shift.id(),
                        shift.day(),
                        shift.startTime(),
                        selectRandomEmployees(shift.requirements(), employees),
                        shift.requirements()
                ))
                .toList();
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
        Chromosome best = getBestChromosome(population);

        List<Chromosome> newPopulation = new ArrayList<>(population.size());
        addBestChromosomes(newPopulation, best);
        generateOffspring(newPopulation, population, employeesByRole);

        return newPopulation;
    }

    /**
     * Adds the best chromosomes to the new population.
     *
     * @param newPopulation the new population
     * @param best          the best chromosome
     */
    private void addBestChromosomes(List<Chromosome> newPopulation, Chromosome best) {
        newPopulation.add(best);
        newPopulation.add(best);
    }

    /**
     * Generates offspring and adds them to the new population.
     *
     * @param newPopulation   the new population
     * @param population      the current population
     * @param employeesByRole the list of employees grouped by role
     */
    private void generateOffspring(List<Chromosome> newPopulation, List<Chromosome> population, Map<String, List<Employee>> employeesByRole) {
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
    }

    /**
     * Selects a chromosome using tournament selection.
     *
     * @param population the population to select from
     * @return the selected chromosome
     */
    private Chromosome tournamentSelection(List<Chromosome> population) {
        List<Chromosome> tournament = IntStream.range(0, tournamentSize)
                .mapToObj(i -> population.get(random.nextInt(population.size())))
                .toList();
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
            return List.of(parent1, parent2);
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
        return List.of(new Chromosome(childGens1, constraintChecker), new Chromosome(childGens2, constraintChecker));
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
        requirements.forEach(req -> {
            validateEmployeesAvailability(req, employees);
            employeesForShift.addAll(selectEmployeesForRequirement(req, employees));
        });
        return employeesForShift;
    }

    /**
     * Validates the availability of employees for a given requirement.
     *
     * @param req       the requirements for the shift
     * @param employees the list of employees grouped by role
     * @throws IllegalArgumentException if no employees with the required role are available
     *                                  or if there are not enough employees with the required role
     */
    private void validateEmployeesAvailability(Requirements req, Map<String, List<Employee>> employees) {
        if (!employees.containsKey(req.role())) {
            throw new IllegalArgumentException("No employees with role " + req.role());
        }
        if (employees.get(req.role()).size() < req.employeesNumber()) {
            throw new IllegalArgumentException("Not enough employees with role " + req.role());
        }
    }

    /**
     * Selects employees for a given requirement.
     *
     * @param req       the requirements for the shift
     * @param employees the list of employees grouped by role
     * @return the selected employees
     */
    private List<Employee> selectEmployeesForRequirement(Requirements req, Map<String, List<Employee>> employees) {
        List<Employee> selectedEmployees = new ArrayList<>();
        List<Employee> availableEmployees = new ArrayList<>(employees.get(req.role()));
        for (int j = 0; j < req.employeesNumber(); j++) {
            int randomIndex = random.nextInt(availableEmployees.size());
            selectedEmployees.add(availableEmployees.get(randomIndex));
            availableEmployees.remove(randomIndex);
        }
        return selectedEmployees;
    }
}