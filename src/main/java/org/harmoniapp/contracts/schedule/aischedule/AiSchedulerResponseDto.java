package org.harmoniapp.contracts.schedule.aischedule;

/**
 * Data Transfer Object for AiSchedulerResponse.
 *
 * @param message  the message of the response
 * @param success  the success of the response
 */
public record AiSchedulerResponseDto(String message, Boolean success) {
}
