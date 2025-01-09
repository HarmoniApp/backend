package org.harmoniapp.geneticalgorithm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Represents a constraint checker for the schedule.
 */
public class ConstraintChecker implements CheckConstraint {
    private static ConstraintChecker instance;

    private final double hardPenalty;
    private final double softPenalty;
    private final int maxShiftPerWeek;

    /**
     * Returns the singleton instance of the ConstraintChecker.
     * If the instance is null, it creates a new ConstraintChecker with default parameters.
     *
     * @return the singleton instance of ConstraintChecker
     */
    public static ConstraintChecker getInstance() {
        if (instance == null) {
            instance = new ConstraintChecker();
        }
        return instance;
    }

    /**
     * Creates a new ConstraintChecker instance with default parameters.
     */
    private ConstraintChecker() {
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
    @Override
    public double checkViolations(List<Gen> chromosome) {
        double violations = chromosome.stream()
                .mapToDouble(this::calculateShiftViolations)
                .sum();

        violations += violationsMaxShiftPerWeek(chromosome);

        List<List<Gen>> shiftsByDay = groupByDay(chromosome);
        violations += checkQuantityOfShiftsPerDay(shiftsByDay);
        violations += checkEarlierShiftNextDay(shiftsByDay);

        return violations;
    }

    /**
     * Calculates the total violations for a single shift.
     *
     * @param shift The shift to check.
     * @return The total violations for the shift.
     */
    private double calculateShiftViolations(Gen shift) {
        return (violationsEmployeeCount(shift) ? hardPenalty : 0)
                + (violationsUniqueEmployee(shift) ? hardPenalty : 0)
                + (violationsRoleMatch(shift) ? hardPenalty : 0);
    }

    /**
     * Checks the quantity of shifts per day.
     *
     * @param shiftsByDay the shifts grouped by day
     * @return the total penalty of the violations
     */
    private double checkQuantityOfShiftsPerDay(List<List<Gen>> shiftsByDay) {
        return shiftsByDay.stream()
                .mapToDouble(this::calculateDayViolations)
                .sum();
    }

    /**
     * Calculates violations for a single day based on employee shift counts.
     *
     * @param shifts List of shifts for the day.
     * @return The calculated violations for the day.
     */
    private double calculateDayViolations(List<Gen> shifts) {
        Map<Employee, Long> employeeShiftCounts = shifts.stream()
                .flatMap(shift -> shift.employees().stream())
                .collect(Collectors.groupingBy(emp -> emp, Collectors.counting()));

        return employeeShiftCounts.values().stream()
                .filter(count -> count > 1)
                .mapToDouble(count -> softPenalty)
                .sum();
    }

    /**
     * Groups the shifts by day.
     *
     * @param chromosome the chromosome to group
     * @return the shifts grouped by day
     */
    private List<List<Gen>> groupByDay(List<Gen> chromosome) {
        return chromosome.stream()
                .collect(Collectors.groupingBy(Gen::day))
                .values().stream()
                .toList();
    }

    /**
     * Checks the violations of the employee count in the shift.
     *
     * @param shift the shift to check
     * @return true if the employee count is violated, false otherwise
     */
    private boolean violationsEmployeeCount(Gen shift) {
        return shift.employees().size() != shift.requirements().stream().mapToInt(Requirements::employeesNumber).sum();
    }

    /**
     * Checks the violations of the unique employee in the shift.
     *
     * @param shift the shift to check
     * @return true if the unique employee is violated, false otherwise
     */
    private boolean violationsUniqueEmployee(Gen shift) {
        return shift.employees().size() != shift.employees().stream().distinct().count();
    }

    /**
     * Checks the violations of the maximum shift per week.
     *
     * @param chromosome the chromosome to check
     * @return the total penalty of the violations
     */
    private double violationsMaxShiftPerWeek(List<Gen> chromosome) {
        Map<Employee, Integer> totalEmployeeCount = new HashMap<>();
        chromosome.stream()
                .flatMap(shift -> shift.employees().stream())
                .forEach(emp -> totalEmployeeCount.merge(emp, 1, Integer::sum));

        return totalEmployeeCount.values().stream()
                .filter(count -> count > maxShiftPerWeek)
                .count() * hardPenalty;
    }

    /**
     * Checks the violations of the role match in the shift.
     *
     * @param shift the shift to check
     * @return true if the role match is violated, false otherwise
     */
    private boolean violationsRoleMatch(Gen shift) {
        return shift.requirements().stream()
                .anyMatch(req -> shift.employees().stream()
                        .filter(emp -> emp.role().equals(req.role()))
                        .count() != req.employeesNumber());
    }

    /**
     * Checks the violations of the earlier shift next day.
     *
     * @param days the shifts grouped by day
     * @return the total penalty of the violations
     */
    private double checkEarlierShiftNextDay(List<List<Gen>> days) {
        return IntStream.range(0, days.size() - 1)
                .filter(i -> !days.get(i).isEmpty() && !days.get(i + 1).isEmpty())
                .mapToDouble(i -> processShiftsForDay(days.get(i), days.get(i + 1)))
                .sum();
    }

    /**
     * Processes the shifts for a single day, checking for violations between current and next day shifts.
     *
     * @param currentDayShifts List of shifts for the current day.
     * @param nextDayShifts    List of shifts for the next day.
     * @return The calculated violations for the day.
     */
    private double processShiftsForDay(List<Gen> currentDayShifts, List<Gen> nextDayShifts) {
        return currentDayShifts.stream()
                .mapToDouble(shift -> processShiftEmployees(shift, nextDayShifts))
                .sum();
    }

    /**
     * Processes employees of a shift, checking if they violate next day shift rules.
     *
     * @param currentDayShift The current day shift.
     * @param nextDayShifts   List of shifts for the next day.
     * @return The calculated violations for the shift.
     */
    private double processShiftEmployees(Gen currentDayShift, List<Gen> nextDayShifts) {
        return currentDayShift.employees().stream()
                .mapToDouble(emp -> checkNextDayShiftsForEmployee(emp, currentDayShift, nextDayShifts))
                .sum();
    }

    /**
     * Checks next day shifts for violations for a specific employee.
     *
     * @param emp             The employee to check.
     * @param currentDayShift The current day shift.
     * @param nextDayShifts   List of shifts for the next day.
     * @return The calculated violations for the employee.
     */
    private double checkNextDayShiftsForEmployee(Employee emp, Gen currentDayShift, List<Gen> nextDayShifts) {
        return nextDayShifts.stream()
                .mapToDouble(nextDayShift -> checkNextDayShiftForEmployee(emp, currentDayShift, nextDayShift))
                .sum();
    }

    /**
     * Checks if an employee violates the next day shift rules.
     *
     * @param emp             The employee to check.
     * @param currentDayShift The current day shift.
     * @param nextDayShift    The next day shift.
     * @return The calculated violations for the employee.
     */
    private double checkNextDayShiftForEmployee(Employee emp, Gen currentDayShift, Gen nextDayShift) {
        return isNextDayShiftEarlier(currentDayShift, nextDayShift) && nextDayShift.employees().contains(emp) ? softPenalty : 0;
    }

    /**
     * Determines if a shift on the next day starts earlier than a shift on the current day.
     *
     * @param currentDayShift The shift from the current day.
     * @param nextDayShift    The shift from the next day.
     * @return true if the next day shift starts earlier, false otherwise.
     */
    private boolean isNextDayShiftEarlier(Gen currentDayShift, Gen nextDayShift) {
        return nextDayShift.startTime().isBefore(currentDayShift.endTime().plusHours(11));
    }
}
