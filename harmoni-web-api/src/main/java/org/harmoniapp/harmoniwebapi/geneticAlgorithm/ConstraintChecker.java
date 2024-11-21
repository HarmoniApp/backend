package org.harmoniapp.harmoniwebapi.geneticAlgorithm;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a constraint checker for the schedule.
 */
@AllArgsConstructor
public class ConstraintChecker {
    private final double hardPenalty;
    private final double softPenalty;
    private final int maxShiftPerWeek;

    /**
     * Creates a new ConstraintChecker instance with default parameters.
     */
    public ConstraintChecker() {
        this.hardPenalty = 0.8;
        this.softPenalty = 0.3;
        this.maxShiftPerWeek = 5;
    }

    /**
     * Checks the violations of the constraints in the chromosome.
     *
     * @param chromosome the chromosome to check
     * @return the total penalty of the violations
     */
    public double checkViolations(List<Shift> chromosome) {
        double violations = 0.0;

        for (Shift shift : chromosome) {
            violations += violationsEmployeeCount(shift) ? hardPenalty : 0;
            violations += violationsUniqueEmployee(shift) ? hardPenalty : 0;
            violations += violationsRoleMatch(shift) ? hardPenalty : 0;
        }

        violations += violationsMaxShiftPerWeek(chromosome);

        List<List<Shift>> shiftsByDay = groupByDay(chromosome);
        violations += checkQuantityOfShiftsPerDay(shiftsByDay);
        violations += checkEarlierShiftNextDay(shiftsByDay);

        return violations;
    }

    /**
     * Checks the quantity of shifts per day.
     *
     * @param shiftsByDay the shifts grouped by day
     * @return the total penalty of the violations
     */
    private double checkQuantityOfShiftsPerDay(List<List<Shift>> shiftsByDay) {
        double violations = 0.0;

        for (List<Shift> shifts : shiftsByDay) {
            Map<Employee, Integer> employeeShifts = new HashMap<>();
            for (Shift shift : shifts) {
                for (Employee emp : shift.getEmployees()) {
                    employeeShifts.put(emp, employeeShifts.getOrDefault(emp, 0) + 1);
                }
            }
            for (int count : employeeShifts.values()) {
                if (count > 1) {
                    violations += hardPenalty;
                }
            }
        }

        return violations;
    }

    /**
     * Groups the shifts by day.
     *
     * @param chromosome the chromosome to group
     * @return the shifts grouped by day
     */
    private List<List<Shift>> groupByDay(List<Shift> chromosome) {
        Map<Integer, List<Shift>> shiftsByDay = new HashMap<>();
        for (Shift shift : chromosome) {
            shiftsByDay.computeIfAbsent(shift.getDay(), k -> new ArrayList<>()).add(shift);
        }
        return new ArrayList<>(shiftsByDay.values());
    }

    /**
     * Checks the violations of the employee count in the shift.
     *
     * @param shift the shift to check
     * @return true if the employee count is violated, false otherwise
     */
    private boolean violationsEmployeeCount(Shift shift) {
        return shift.getEmployees().size() != shift.getRequirements().stream().mapToInt(Requirements::employeesNumber).sum();
    }

    /**
     * Checks the violations of the unique employee in the shift.
     *
     * @param shift the shift to check
     * @return true if the unique employee is violated, false otherwise
     */
    private boolean violationsUniqueEmployee(Shift shift) {
        return shift.getEmployees().size() != shift.getEmployees().stream().distinct().count();
    }

    /**
     * Checks the violations of the maximum shift per week.
     *
     * @param chromosome the chromosome to check
     * @return the total penalty of the violations
     */
    private double violationsMaxShiftPerWeek(List<Shift> chromosome) {
        double violations = 0.0;

        Map<Employee, Integer> totalEmployeeCount = new HashMap<>();
        for (Shift shift : chromosome) {
            for (Employee emp : shift.getEmployees()) {
                totalEmployeeCount.put(emp, totalEmployeeCount.getOrDefault(emp, 0) + 1);
            }
        }

        for (int count : totalEmployeeCount.values()) {
            if (count > maxShiftPerWeek) {
                violations += hardPenalty;
            }
        }

        return violations;
    }

    /**
     * Checks the violations of the role match in the shift.
     *
     * @param shift the shift to check
     * @return true if the role match is violated, false otherwise
     */
    private boolean violationsRoleMatch(Shift shift) {
        for (Requirements req : shift.getRequirements()) {
            long count = shift.getEmployees().stream()
                    .filter(emp -> emp.role().equals(req.role()))
                    .count();

            if (count != req.employeesNumber()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks the violations of the earlier shift next day.
     *
     * @param days the shifts grouped by day
     * @return the total penalty of the violations
     */
    private double checkEarlierShiftNextDay(List<List<Shift>> days) {
        double violations = 0.0;

        for (int i = 0; i < days.size() - 1; i++) {
            List<Shift> currentDayShifts = days.get(i);
            List<Shift> nextDayShifts = days.get(i + 1);

            for (int j =0; j < currentDayShifts.size(); j++) {
                for (Employee emp : currentDayShifts.get(j).getEmployees()) {
                    for (int k=0; k < j; k++) {
                        if (nextDayShifts.get(k).getEmployees().contains(emp)) {
                            violations += softPenalty;
                            break;
                        }
                    }
                }
            }
        }

        return violations;
    }
}
