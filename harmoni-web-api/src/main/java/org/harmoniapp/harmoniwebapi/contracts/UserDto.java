package org.harmoniapp.harmoniwebapi.contracts;

import org.harmoniapp.harmonidata.enums.ContractType;
import org.harmoniapp.harmonidata.enums.Language;
import org.harmoniapp.harmonidata.enums.Role;

import java.util.Date;
import java.util.Set;

public record UserDto(long id, String firstname, String lastname, String email,
                      ContractType contract, Date contractSignature, Date contractExpiration,
                      AddressDto residence, AddressDto workAddress, long supervisorId,
                      String phone, String employeeId, Set<Role> roles, Set<Language> languages) {
}
