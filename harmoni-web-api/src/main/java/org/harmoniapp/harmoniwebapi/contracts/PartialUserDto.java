package org.harmoniapp.harmoniwebapi.contracts;

import org.harmoniapp.harmonidata.entities.User;

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
                             Set<LanguageDto> languages) {

    public static PartialUserDto fromEntity(User user) {
        return new PartialUserDto(
                user.getId(),
                user.getFirstname(),
                user.getSurname(),
                user.getLanguages().stream()
                        .map(p -> new LanguageDto(p.getId(), p.getName(), p.getCode()))
                        .collect(Collectors.toSet())
        );
    }
}
