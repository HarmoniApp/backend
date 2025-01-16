package org.harmoniapp.geneticalgorithm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConstraintCheckerTest {

    private ConstraintChecker constraintChecker;

    @BeforeEach
    public void setUp() {
        constraintChecker = ConstraintChecker.getInstance();
    }

    @Test
    public void getInstanceTest() {
        ConstraintChecker instance = ConstraintChecker.getInstance();
        assertNotNull(instance);
    }

    @Test
    public void checkViolationsTest() {
        Gen gen = mock(Gen.class);
        List<Gen> chromosome = List.of(gen);
        Employee employee = new Employee("EMP01", "test");
        when(gen.employees()).thenReturn(List.of(employee));
        when(gen.requirements()).thenReturn(List.of(mock(Requirements.class)));
        when(gen.day()).thenReturn(1);
        when(gen.startTime()).thenReturn(mock(java.time.LocalTime.class));
        when(gen.endTime()).thenReturn(mock(java.time.LocalTime.class));

        double violations = constraintChecker.checkViolations(chromosome);

        assertTrue(violations >= 0);
    }
}