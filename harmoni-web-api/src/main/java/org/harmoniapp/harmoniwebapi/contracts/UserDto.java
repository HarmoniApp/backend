package org.harmoniapp.harmoniwebapi.contracts;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.harmoniapp.harmonidata.entities.ContractType;
import org.harmoniapp.harmonidata.entities.Role;
import org.harmoniapp.harmonidata.entities.User;
import org.springframework.cglib.core.Local;

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

        @NotEmpty(message = "First name cannot be empty")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z]+$", message = "Firstname must contain only letters")
        String firstname,

        @NotEmpty(message = "Surname cannot be empty")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z -]+$", message = "Surname must contain only letters, spaces and dashes")
        String surname,

        @NotEmpty(message = "Email cannot be empty")
        @Email(message = "Email should be valid")
        @Size(max = 320, message = "Email must be less than or equal to 320 characters")
        String email,

        //TODO: validation?
        String password,

        @NotNull(message = "Contract type cannot be null")
        @JsonProperty("contract_type") ContractType contractType,

        @NotNull(message = "Contract signature date cannot be null")
        @JsonProperty("contract_signature") LocalDate contractSignature,

        @NotNull(message = "Contract expiration date cannot be null")
        @JsonProperty("contract_expiration") LocalDate contractExpiration,

        @NotNull(message = "Residence cannot be null")
        @Valid
        AddressDto residence,

        @NotNull(message = "Work address cannot be null")
        @JsonProperty("work_address") AddressDto workAddress,

        @JsonProperty("supervisor_id") Long supervisorId,

        @NotEmpty(message = "Phone number cannot be empty")
        @Pattern(regexp = "^(\\+?\\d{1,3}[\\s]?)?(\\d[\\s]?){9,15}$", message = "Phone number must be between 9 and 15 digits, and can contain spaces and a leading '+'")
        @JsonProperty("phone_number") String phoneNumber,

        @NotEmpty(message = "Employee ID cannot be empty")
        @Size(max = 20, message = "Employee ID must be less than or equal to 20 characters")
        @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "Employee ID must contain only letters, numbers, and dashes")
        @JsonProperty("employee_id") String employeeId,

        String photo,

        @JsonProperty("is_active") boolean isActive,

        @JsonProperty("available_absence_days") int availableAbsenceDays,
        @JsonProperty("unused_absence_days") Integer unusedAbsenceDays,

        @NotEmpty(message = "Roles cannot be null or empty")
        List<Role> roles,

        @NotEmpty(message = "Languages cannot be null or empty")
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
                user.getPhoto(),
                user.isActive(),
                user.getAvailableAbsenceDays(),
                user.getUnusedAbsenceDays(),
                user.getRoles().stream().toList(),
                user.getLanguages().stream().map(p -> new LanguageDto(p.getId(), p.getName(), p.getCode())).toList()
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
                this.photo,
                0,
                LocalDate.now(),
                this.isActive,
                this.availableAbsenceDays,
                0,
                null,
                null,
                null
        );
    }
}
