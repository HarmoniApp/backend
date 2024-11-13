package org.harmoniapp.autoscheduling;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Shift {
    private int id;
    private int day;
    private List<Employee> employees;
    private List<Requirements> requirements;
}
