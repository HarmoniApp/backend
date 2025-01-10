package org.harmoniapp.geneticalgorithm;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class App {

    /**
     * Main method.
     * Example of how to use the genetic algorithm to solve the employee scheduling problem.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < 42; i++) {
            Employee employee = new Employee("Employee_" + i, "role_" + i % 3);
            employees.add(employee);
        }

        List<Requirements> requirements = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Requirements requirement = new Requirements("role_" + i, 3);
            requirements.add(requirement);
        }

        List<Gen> shifts = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 3; j++) {
                LocalTime start = LocalTime.of((6 + 8 * j) % 24, 0);
                LocalTime end = start.plusHours(8);
                Gen shift = new Gen(j, i, start, end, new ArrayList<>(), requirements);
                shifts.add(shift);
            }
        }

        Map<String, List<Employee>> employeesByRole = employees.stream().collect(Collectors.groupingBy(Employee::role));

        GeneticAlgorithm GA = new GeneticAlgorithm(50, 10, 10000,
                0.02, 0.7,
                new Random(), 100, List.of(new DefaultGenerationObserver()));
        Chromosome result = GA.run(shifts, employeesByRole);
        System.out.println(result.getGens());
    }
}
