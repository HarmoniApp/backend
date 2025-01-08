package org.harmoniapp.services.schedule.aischedule;

import lombok.extern.log4j.Log4j2;
import org.harmoniapp.geneticalgorithm.GenerationObserver;

/**
 * Observer implementation that logs the progress and fitness of each generation.
 */
@Log4j2
public record LogGenerationObserver() implements GenerationObserver {

    /**
     * Logs the progress and fitness of the current generation.
     *
     * @param progress the progress percentage of the current generation
     * @param fitness  the fitness value of the current generation
     */
    @Override
    public void onGenerationUpdate(double progress, double fitness) {
        log.trace("Postęp: {}%, Dopasowanie: {}", progress, fitness);
    }
}
