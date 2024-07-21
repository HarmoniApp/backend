package org.harmoniapp.harmoniwebapi.contracts;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.harmoniapp.harmonidata.entities.ContractType;
import org.harmoniapp.harmonidata.entities.Language;
import org.harmoniapp.harmonidata.entities.Role;
import org.harmoniapp.harmonidata.entities.User;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record UserDto(
        long id,
        String firstname,
        String surname,
        String email,
        String password,
        @JsonProperty("contract_type") ContractType contractType,
        @JsonProperty("contract_signature") Date contractSignature,
        @JsonProperty("contract_expiration") Date contractExpiration,
        AddressDto residence,
        @JsonProperty("work_address") AddressDto workAddress,
        @JsonProperty("supervisor_id") Long supervisorId,
        @JsonProperty("phone_number") String phoneNumber,
        @JsonProperty("employee_id") String employeeId,
        List<Role> roles,
        List<Language> languages) {

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
                user.getLanguages().stream().toList()
        );
    }
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
