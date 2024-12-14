package org.harmoniapp.contracts.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.harmoniapp.contracts.profile.LanguageDto;
import org.harmoniapp.entities.user.User;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Data Transfer Object for User and their associated languages.
 *
 * @param id        the unique identifier of the user
 * @param firstname the first name of the user
 * @param surname  the surname of the user
 * @param languages the set of languages associated with the user
 */
public record PartialUserDto(long id,
                             String firstname,
                             String surname,
                             String photo, //TODO: Verify if this field is needed
                             Set<LanguageDto> languages,
                             @JsonProperty("employee_id") String employeeId) {

    public static PartialUserDto fromEntity(User user) {
        return new PartialUserDto(
                user.getId(),
                user.getFirstname(),
                user.getSurname(),
                user.getPhoto(),
                user.getLanguages().stream()
                        .map(LanguageDto::fromEntity)
                        .collect(Collectors.toSet()),
                user.getEmployeeId()
        );
    }
}
