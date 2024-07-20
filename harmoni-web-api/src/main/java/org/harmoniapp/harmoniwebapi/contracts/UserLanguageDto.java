package org.harmoniapp.harmoniwebapi.contracts;

import java.util.Set;

/**
 * Data Transfer Object for User and their associated languages.
 *
 * @param id        the unique identifier of the user
 * @param firstName the first name of the user
 * @param lastName  the last name of the user
 * @param languages the set of languages associated with the user
 */
public record UserLanguageDto(long id, String firstName, String lastName, Set<String> languages) {
}
