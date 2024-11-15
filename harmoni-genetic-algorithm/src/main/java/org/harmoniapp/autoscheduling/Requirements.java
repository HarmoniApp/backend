package org.harmoniapp.autoscheduling;

import lombok.Data;

/**
 * Represents the requirements for a shift.
 */
@Data
public class Requirements {
    private final String role;
    private final int employeesNumber;
}
