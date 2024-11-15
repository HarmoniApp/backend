package org.harmoniapp.harmoniwebapi.contracts;

/**
 * Data Transfer Object (DTO) for login responses.
 *
 * @param status   the status of the login attempt (e.g., "success", "failure").
 * @param jwtToken the JWT token issued after a successful login.
 */
public record LoginResponseDto(String status, String jwtToken, String path) {
}
