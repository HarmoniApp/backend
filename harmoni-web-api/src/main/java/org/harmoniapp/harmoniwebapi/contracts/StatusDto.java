package org.harmoniapp.harmoniwebapi.contracts;

import org.harmoniapp.harmonidata.entities.Status;

/**
 * Data Transfer Object for Status.
 *
 * @param id   the unique identifier of the status
 * @param name the name of the status
 */
public record StatusDto(long id, String name) {

    /**
     * Converts a Status entity to a StatusDto.
     *
     * @param status the Status entity to be converted
     * @return a StatusDto representing the provided Status entity
     */
    public static StatusDto fromEntity(Status status) {
        return new StatusDto(
                status.getId(),
                status.getName()
        );
    }
}
