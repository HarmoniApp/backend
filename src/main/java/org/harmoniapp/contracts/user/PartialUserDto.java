package org.harmoniapp.contracts.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.harmoniapp.contracts.profile.LanguageDto;
import org.harmoniapp.entities.user.User;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Data Transfer Object for User and their associated languages.
 * This record encapsulates essential information about a user and their languages.
 *
 * @param id        the unique identifier of the user
 * @param firstname the first name of the user
 * @param surname   the surname of the user
 * @param languages the set of languages associated with the user
 */
@Builder
public record PartialUserDto(long id,
                             String firstname,
                             String surname,
                             Set<LanguageDto> languages,
                             @JsonProperty("employee_id") String employeeId) {

    public static PartialUserDto fromEntity(User user) {
        return PartialUserDto.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .surname(user.getSurname())
                .languages(getLanguages(user))
                .employeeId(user.getEmployeeId())
                .build();
    }

    private static Set<LanguageDto> getLanguages(User user) {
        return user.getLanguages().stream()
                .map(LanguageDto::fromEntity)
                .collect(Collectors.toSet());
    }
}
