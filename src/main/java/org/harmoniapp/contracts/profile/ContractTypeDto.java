package org.harmoniapp.contracts.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.harmoniapp.entities.profile.ContractType;

/**
 * Data Transfer Object for ContractType.
 *
 * @param id   the unique identifier of the ContractType
 * @param name the name of the ContractType
 */
public record ContractTypeDto(
        long id,

        @NotEmpty(message = "Nazwa nie może być pusta")
        @Pattern(regexp = "^[A-Za-Ā-ɏØ-öø-ÿ'\\-\\s]+$", message = "Nazwa musi zawierać tylko litery, spacje, apostrofy i myślniki")
        String name,

        @Min(value = 0, message = "Dni nieobecności muszą być liczbą nieujemną")
        @JsonProperty("absence_days") int absenceDays) {

    /**
     * Converts a ContractType entity to a ContractTypeDto.
     *
     * @param contractType the ContractType entity to convert
     * @return the resulting ContractTypeDto
     */
    public static ContractTypeDto fromEntity(ContractType contractType) {
        return new ContractTypeDto(
                contractType.getId(),
                contractType.getName(),
                contractType.getAbsenceDays()
        );
    }

    /**
     * Converts a ContractTypeDto to a ContractType entity.
     *
     * @return the resulting ContractType entity
     */
    public ContractType toEntity() {
        return new ContractType(this.id, this.name, this.absenceDays);
    }
}
