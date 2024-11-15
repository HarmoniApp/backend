package org.harmoniapp.harmoniwebapi.contracts;

/**
 * Data Transfer Object (DTO) for login request.
 *
 * @param username the username or email of the user attempting to log in.
 * @param password the password of the user attempting to log in.
 */
public record LoginRequestDto(String username, String password) {
}
