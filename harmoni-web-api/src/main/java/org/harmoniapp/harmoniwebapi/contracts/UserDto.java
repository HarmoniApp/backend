package org.harmoniapp.harmoniwebapi.contracts;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.harmoniapp.harmonidata.entities.ContractType;
import org.harmoniapp.harmonidata.entities.Role;
import org.harmoniapp.harmonidata.entities.User;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * Data Transfer Object (DTO) for User.
 *
 * @param id                 The unique identifier for the user.
 * @param firstname          The first name of the user.
 * @param surname            The surname of the user.
 * @param email              The email address of the user.
 * @param password           The password of the user (not serialized).
 * @param contractType       The type of contract for the user.
 * @param contractSignature  The date when the contract was signed.
 * @param contractExpiration The date when the contract expires.
 * @param residence          The residential address of the user.
 * @param workAddress        The work address of the user.
 * @param supervisorId       The ID of the user's supervisor, if applicable.
 * @param phoneNumber        The phone number of the user.
 * @param employeeId         The employee ID of the user.
 * @param roles              The roles assigned to the user.
 * @param languages          The languages known by the user.
 */
public record UserDto(
        long id,
        String firstname,
        String surname,
        String email,
        String password,
        @JsonProperty("contract_type") ContractType contractType,
        @JsonProperty("contract_signature") LocalDate contractSignature,
        @JsonProperty("contract_expiration") LocalDate contractExpiration,
        AddressDto residence,
        @JsonProperty("work_address") AddressDto workAddress,
        @JsonProperty("supervisor_id") Long supervisorId,
        @JsonProperty("phone_number") String phoneNumber,
        @JsonProperty("employee_id") String employeeId,
        List<Role> roles,
        List<LanguageDto> languages) {

    /**
     * Converts a User entity to a UserDto.
     *
     * @param user The User entity to be converted.
     * @return A UserDto representing the User entity.
     */
    public static UserDto fromEntity(User user) {
        return new UserDto(
                user.getId(),
                user.getFirstname(),
                user.getSurname(),
                user.getEmail(),
                null,
                user.getContractType(),
                user.getContractSignature(),
                user.getContractExpiration(),
                AddressDto.fromEntity(user.getResidence()),
                AddressDto.fromEntity(user.getWorkAddress()),
                (user.getSupervisor() != null) ? user.getSupervisor().getId() : null,
                user.getPhoneNumber(),
                user.getEmployeeId(),
                user.getRoles().stream().toList(),
                user.getLanguages().stream().map(p -> new LanguageDto(p.getId(), p.getName())).toList()
        );
    }

    /**
     * Converts this UserDto to a User entity.
     *
     * @return A User entity representing this UserDto.
     */
    public User toEntity() {
        return new User(
                this.id,
                this.firstname,
                this.surname,
                this.email,
                this.password,
                this.contractType,
                this.contractSignature,
                this.contractExpiration,
                null,
                null,
                null,
                this.phoneNumber,
                this.employeeId,
                null,
                null
        );
    }
}
