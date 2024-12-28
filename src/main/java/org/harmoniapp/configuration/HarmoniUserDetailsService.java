package org.harmoniapp.configuration;


import lombok.RequiredArgsConstructor;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.repositories.RepositoryCollector;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for loading user-specific data.
 * Implements the UserDetailsService interface to provide user details for authentication.
 */
@Service
@RequiredArgsConstructor
public class HarmoniUserDetailsService implements UserDetailsService {
    private final RepositoryCollector repositoryCollector;

    /**
     * Loads the user details by username.
     *
     * @param username the username identifying the user whose data is required
     * @return a fully populated UserDetails object
     * @throws UsernameNotFoundException if the user could not be found or the user is inactive
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = fetchUserByUsername(username);
        validateUser(user);
        List<GrantedAuthority> authorities = getUserAuthorities(user);
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    /**
     * Fetches the user by username.
     *
     * @param username the username identifying the user whose data is required
     * @return the User object
     * @throws UsernameNotFoundException if the user could not be found
     */
    private User fetchUserByUsername(String username) {
        return repositoryCollector.getUsers().findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User details not found for the user: " + username));
    }

    /**
     * Validates the user.
     *
     * @param user the User object to validate
     * @throws UsernameNotFoundException if the user is inactive, has no password, or is locked due to multiple failed login attempts
     */
    private void validateUser(User user) {
        if (!user.getIsActive()) {
            throw new UsernameNotFoundException("User account is inactive.");
        }
        if (user.getPassword() == null) {
            throw new UsernameNotFoundException("Invalid credentials.");
        }
        if (user.getFailedLoginAttempts() >= 3) {
            throw new UsernameNotFoundException("User account is locked due to multiple failed login attempts.");
        }
    }

    /**
     * Gets the authorities granted to the user.
     *
     * @param user the User object
     * @return a list of granted authorities
     */
    private List<GrantedAuthority> getUserAuthorities(User user) {
        if (user.getRoles().stream().anyMatch(r -> r.getName().equalsIgnoreCase("ADMIN"))) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }
}
