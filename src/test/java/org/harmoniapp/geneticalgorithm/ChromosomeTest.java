package org.harmoniapp.geneticalgorithm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ChromosomeTest {

    private Chromosome chromosome;

    @Mock
    private Gen gen;

    @Mock
    private CheckConstraint checker;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        chromosome = new Chromosome(List.of(gen), checker, 0.0);
    }

    @Test
    public void getGensTest() {
        List<Gen> gens = chromosome.getGens();
        assertNotNull(gens);
        assertEquals(1, gens.size());
    }

    @Test
    public void evaluateFitnessTest() {
        when(checker.checkViolations(anyList())).thenReturn(0.0);
        chromosome.evaluateFitness();
        assertEquals(1.0, chromosome.getFitness());
    }
}