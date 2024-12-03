package org.harmoniapp.harmoniwebapi.contracts.AiSchedule;

/**
 * Data Transfer Object for representing the progress of the AI schedule generation.
 *
 * @param generation the current generation number
 * @param fitness    the fitness score of the current generation
 */
public record GeneratingProgressDto(int generation, double fitness) {
}
