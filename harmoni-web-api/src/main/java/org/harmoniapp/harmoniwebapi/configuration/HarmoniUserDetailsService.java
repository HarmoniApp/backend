package org.harmoniapp.harmoniwebapi.configuration;


import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.Role;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Custom implementation of {@link UserDetailsService} for loading user details from the database.
 * This service retrieves user information using the {@link RepositoryCollector} and assigns roles
 * based on user roles within the system.
 *
 * <p>If the user has a "sup" role, they are assigned the "ROLE_ADMIN", otherwise they are assigned the "ROLE_USER".</p>
 *
 * @see UserDetailsService
 * @see RepositoryCollector
 */
@Service
@RequiredArgsConstructor
public class HarmoniUserDetailsService implements UserDetailsService {
    private final RepositoryCollector repositoryCollector;

    /**
     * Loads user-specific data by username (which is the user's email in this case).
     *
     * <p>This method fetches the user from the database, verifies if the user exists, and assigns roles based on
     * the user's roles in the system. If the user has a "sup" role, they are granted "ROLE_ADMIN", otherwise "ROLE_USER".</p>
     *
     * @param username the username identifying the user whose data is required (email in this case).
     * @return a fully populated {@link UserDetails} object.
     * @throws UsernameNotFoundException if no user is found with the given username.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        org.harmoniapp.harmonidata.entities.User user = repositoryCollector.getUsers().findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User details not found for the user: " + username));

        List<GrantedAuthority> authorities;
        if (user.getRoles().stream().anyMatch(Role::isSup)) {
            authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }
}
