package org.harmoniapp.geneticalgorithm;

/**
 * Listener interface for receiving updates during the genetic algorithm's execution.
 */
public interface GenerationListener {
    void onGenerationUpdate(int generation, double fitness);
}
