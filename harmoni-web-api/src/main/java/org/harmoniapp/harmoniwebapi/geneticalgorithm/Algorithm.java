package org.harmoniapp.harmoniwebapi.geneticalgorithm;

import java.util.List;
import java.util.Map;

public interface Algorithm {
    Chromosome run(List<Gen> shifts, Map<String, List<Employee>> employees);
}
