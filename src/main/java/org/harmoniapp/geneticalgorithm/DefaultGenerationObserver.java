package org.harmoniapp.geneticalgorithm;

/**
 * Default implementation of the GenerationObserver interface.
 */
public record DefaultGenerationObserver() implements GenerationObserver {

    /**
     * Called to update the observer with the current generation progress and fitness.
     *
     * @param progress the current progress of the generation
     * @param fitness the current fitness value of the generation
     */
    @Override
    public void onGenerationUpdate(double progress, double fitness) {
        System.out.println("Progress: " + progress + "%, Fitness: " + fitness);
    }
}
