package org.harmoniapp.contracts.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import org.harmoniapp.contracts.profile.AddressDto;
import org.harmoniapp.contracts.profile.ContractTypeDto;
import org.harmoniapp.contracts.profile.LanguageDto;
import org.harmoniapp.contracts.profile.RoleDto;
import org.harmoniapp.entities.profile.ContractType;
import org.harmoniapp.entities.profile.Language;
import org.harmoniapp.entities.profile.Role;
import org.harmoniapp.entities.user.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

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
@Builder
public record UserDto(
        Long id,

        @NotEmpty(message = "Imię nie może być puste")
        @Size(min = 2, max = 50, message = "Imię musi zawierać od 2 do 50 znaków")
        @Pattern(regexp = "^[A-Za-zĀ-ɏØ-öø-ÿ'\\-\\s]+$", message = "Imię musi zawierać tylko litery, spacje i myślniki")
        String firstname,

        @NotEmpty(message = "Nazwisko nie może być puste")
        @Size(min = 2, max = 50, message = "Nazwisko musi zawierać od 2 do 50 znaków")
        @Pattern(regexp = "^[A-Za-zĀ-ɏØ-öø-ÿ'\\-\\s]+$", message = "Nazwisko musi zawierać tylko litery, spacje i myślniki")
        String surname,

        @NotEmpty(message = "Email nie może być pusty")
        @Email(message = "Email musi być poprawny")
        @Size(max = 320, message = "Email musi zawierać mniej niż 320 znaków")
        String email,

        String password,

        @NotNull(message = "Typ umowy nie może być pusty")
        @JsonProperty("contract_type") ContractTypeDto contractType,

        @NotNull(message = "Data podpisania umowy nie może być pusta")
        @JsonProperty("contract_signature") LocalDate contractSignature,

        @JsonProperty("contract_expiration") LocalDate contractExpiration,

        @NotNull(message = "Adres zamieszkania nie może być pusty")
        @Valid
        AddressDto residence,

        @NotNull(message = "Adres pracy nie może być pusty")
        @JsonProperty("work_address") AddressDto workAddress,

        @JsonProperty("supervisor_id") Long supervisorId,

        @NotEmpty(message = "Numer telefonu nie może być pusty")
        @Pattern(regexp = "^(\\+?\\d{1,3}[\\s]?)?(\\d[\\s]?){9,15}$", message = "Numer telefonu musi być poprawny")
        @JsonProperty("phone_number") String phoneNumber,

        @NotEmpty(message = "ID pracownika nie może być puste")
        @Size(max = 20, message = "ID pracownika musi zawierać mniej niż 20 znaków")
        @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "ID pracownika musi zawierać tylko litery, cyfry i myślniki")
        @JsonProperty("employee_id") String employeeId,

        String photo,

        @JsonProperty("is_active") Boolean isActive,

        @JsonProperty("available_absence_days") Integer availableAbsenceDays,
        @JsonProperty("unused_absence_days") Integer unusedAbsenceDays,

        @NotEmpty(message = "Role nie mogą być puste")
        List<RoleDto> roles,

        @NotEmpty(message = "Języki nie mogą być puste")
        List<LanguageDto> languages) {

    /**
     * Converts a User entity to a UserDto.
     *
     * @param user The User entity to be converted.
     * @return A UserDto representing the User entity.
     */
    public static UserDto fromEntity(User user) {
        return fromEntity(user, null);
    }

    /**
     * Converts a User entity to a UserDto.
     *
     * @param user     The User entity to be converted.
     * @param password The user password in plain text
     * @return A UserDto representing the User entity.
     */
    public static UserDto fromEntity(User user, String password) {
        return UserDto.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .surname(user.getSurname())
                .email(user.getEmail())
                .password(password)
                .contractType(ContractTypeDto.fromEntity(user.getContractType()))
                .contractSignature(user.getContractSignature())
                .contractExpiration(user.getContractExpiration())
                .residence(AddressDto.fromEntity(user.getResidence()))
                .workAddress(AddressDto.fromEntity(user.getWorkAddress()))
                .supervisorId((user.getSupervisor() != null) ? user.getSupervisor().getId() : null)
                .phoneNumber(user.getPhoneNumber())
                .employeeId(user.getEmployeeId())
                .photo(user.getPhoto())
                .isActive(user.getIsActive())
                .availableAbsenceDays(user.getAvailableAbsenceDays())
                .unusedAbsenceDays(user.getUnusedAbsenceDays())
                .roles(getRoleDtos(user.getRoles()))
                .languages(getLanguageDtos(user.getLanguages()))
                .build();
    }

    /**
     * Converts this UserDto to a User entity.
     *
     * @return A User entity representing this UserDto.
     */
    public User toEntity() {
        return User.builder()
                .id(this.id)
                .firstname(this.firstname)
                .surname(this.surname)
                .email(this.email)
                .password(this.password)
                .contractType(this.contractType != null ? this.contractType.toEntity() : null)
                .contractSignature(this.contractSignature)
                .contractExpiration(this.contractExpiration)
                .phoneNumber(this.phoneNumber)
                .employeeId(this.employeeId)
                .photo(this.photo)
                .failedLoginAttempts(0)
                .passwordExpirationDate(LocalDate.now().minusDays(1))
                .isActive(this.isActive)
                .availableAbsenceDays(this.availableAbsenceDays)
                .build();
    }

    private static List<RoleDto> getRoleDtos(Set<Role> role) {
        if (role == null) {
            return null;
        }
        return role.stream().map(RoleDto::fromEntity).toList();
    }

    private static List<LanguageDto> getLanguageDtos(Set<Language> languages) {
        if (languages == null) {
            return null;
        }
        return languages.stream().map(LanguageDto::fromEntity).toList();
    }
}
