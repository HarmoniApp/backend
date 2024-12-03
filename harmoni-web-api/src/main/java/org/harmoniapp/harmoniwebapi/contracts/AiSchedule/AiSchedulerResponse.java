package org.harmoniapp.harmoniwebapi.contracts.AiSchedule;

/**
 * Data Transfer Object for AiSchedulerResponse.
 *
 * @param message  the message of the response
 * @param success  the success of the response
 */
public record AiSchedulerResponse(String message, Boolean success) {
}
