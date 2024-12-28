package org.harmoniapp.services.schedule.aischedule;

import org.harmoniapp.contracts.schedule.aischedule.GeneratingProgressDto;
import org.harmoniapp.geneticalgorithm.GenerationObserver;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * A WebSocket observer that sends generation updates to a specific client.
 *
 * @param messagingTemplate the messaging template used to send messages
 * @param receiverId the ID of the client to receive the updates
 */
public record WsGenerationObserver(SimpMessagingTemplate messagingTemplate,
                                   long receiverId) implements GenerationObserver {

    /**
     * Sends a generation update to the client.
     *
     * @param progress the current progress of the generation
     * @param fitness the current fitness value of the generation
     */
    @Override
    public void onGenerationUpdate(double progress, double fitness) {
        GeneratingProgressDto response = new GeneratingProgressDto(progress, fitness);
        messagingTemplate.convertAndSend("/client/fitness/" + receiverId, response);
    }
}
