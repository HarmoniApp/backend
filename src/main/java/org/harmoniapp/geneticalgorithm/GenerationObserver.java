package org.harmoniapp.geneticalgorithm;

/**
 * Listener interface for receiving updates during the genetic algorithm's execution.
 */
public interface GenerationObserver {
    void onGenerationUpdate(double progress, double fitness);
}
