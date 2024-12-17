package org.harmoniapp.geneticalgorithm;

/**
 * Represents the requirements for a shift.
 *
 * @param role the role required for the shift
 * @param employeesNumber the number of employees required for the shift
 */
public record Requirements(String role, int employeesNumber) {
}
