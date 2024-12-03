package org.harmoniapp.harmoniwebapi.geneticalgorithm;

import java.util.List;

public interface CheckConstraint {
    double checkViolations(List<Gen> chromosome);
}
