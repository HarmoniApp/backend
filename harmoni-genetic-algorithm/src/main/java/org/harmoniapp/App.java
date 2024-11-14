package org.harmoniapp;

import org.harmoniapp.autoscheduling.*;
import sun.util.resources.LocaleData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) {
        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            Employee employee = new Employee("Employee_" + i, "role_" + i % 3);
            employees.add(employee);
        }

        List<Requirements> requirements = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Requirements requirement = new Requirements("role_" + i, 2);
            requirements.add(requirement);
        }

        List<Shift> shifts = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 3; j++) {
                Shift shift = new Shift(j, i, new ArrayList<>(), requirements);
                shifts.add(shift);
            }
        }

        Map<String, List<Employee>> employeesByRole = employees.stream().collect(Collectors.groupingBy(Employee::getRole));

        GeneticAlgorithm GA = new GeneticAlgorithm(50, 20000, 0.02, 0.6);
        List<Shift> result = GA.run(shifts, employeesByRole);
        System.out.println(result);
    }
}
