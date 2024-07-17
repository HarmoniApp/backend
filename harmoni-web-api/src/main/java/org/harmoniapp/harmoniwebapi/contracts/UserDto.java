package org.harmoniapp.harmoniwebapi.contracts;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.harmoniapp.harmonidata.enums.ContractType;
import org.harmoniapp.harmonidata.enums.Language;
import org.harmoniapp.harmonidata.enums.Role;

import java.util.Date;
import java.util.Set;

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
        @JsonProperty("supervisor_id") long supervisorId,
        @JsonProperty("phone_number") String phoneNumber,
        @JsonProperty("employee_id") String employeeId,
        Set<Role> roles,
        Set<Language> languages) {
}
