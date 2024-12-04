package org.harmoniapp.configuration;

/**
 * A simple record representing a security principle with an ID and username.
 *
 * <p>This record is used to encapsulate basic user information such as their ID and username.</p>
 *
 * @param id        the unique identifier of the user.
 * @param username  the username (which is the user's email in this case) of the user.
 */
public record Principle(Long id, String username) {
}
