package org.harmoniapp.harmoniwebapi.contracts;

import org.harmoniapp.harmonidata.entities.ContractType;

/**
 * Data Transfer Object for ContractType.
 *
 * @param id    the unique identifier of the ContractType
 * @param name  the name of the ContractType
 */
public record ContractTypeDto(long id, String name) {

    /**
     * Converts a ContractType entity to a ContractTypeDto.
     *
     * @param contractType the ContractType entity to convert
     * @return the resulting ContractTypeDto
     */
    public static ContractTypeDto fromEntity(ContractType contractType) {
        return new ContractTypeDto(
                contractType.getId(),
                contractType.getName()
        );
    }

    /**
     * Converts a ContractTypeDto to a ContractType entity.
     *
     * @return the resulting ContractType entity
     */
    public ContractType toEntity() {
        return new ContractType(
                this.id,
                this.name
        );
    }
}
