package org.harmoniapp.autoscheduling;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class Shift {
    private final int id;
    private final int day;
    private List<Employee> employees;
    private final List<Requirements> requirements;

    public Shift(int id, int day, List<Requirements> requirements) {
        this.id = id;
        this.day = day;
        this.employees = new ArrayList<>();
        this.requirements = requirements;
    }
}
