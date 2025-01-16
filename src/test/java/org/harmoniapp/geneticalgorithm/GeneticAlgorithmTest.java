package org.harmoniapp.geneticalgorithm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GeneticAlgorithmTest {

    private GeneticAlgorithm geneticAlgorithm;

    @Mock
    private Gen gen;

    @Mock
    private Employee employee;

    @Mock
    private Requirements requirements;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        geneticAlgorithm = new GeneticAlgorithm();
    }

    @Test
    public void runTest() {
        List<Gen> shifts = List.of(gen, gen);
        Map<String, List<Employee>> employees = Map.of("role", List.of(employee, employee));
        Chromosome result = geneticAlgorithm.run(shifts, employees);
        assertNotNull(result);
    }

    @Test
    public void addObserverTest() {
        GenerationObserver observer = mock(GenerationObserver.class);
        geneticAlgorithm.addObserver(observer);
        assertTrue(geneticAlgorithm.getObservers().contains(observer));
    }

    @Test
    public void notifyObserversTest() {
        GenerationObserver observer = mock(GenerationObserver.class);
        geneticAlgorithm.addObserver(observer);
        Chromosome bestChromosome = mock(Chromosome.class);
        when(bestChromosome.getFitness()).thenReturn(1.0);
        geneticAlgorithm.notifyObservers(0, bestChromosome);
        verify(observer, times(1)).onGenerationUpdate(anyDouble(), eq(1.0));
    }
}