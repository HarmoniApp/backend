package org.harmoniapp.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Authentication provider for user password authentication in production environment.
 * This provider uses {@link UserDetailsService} to load user details and {@link PasswordEncoder}
 * to verify passwords.
 *
 * <p>It supports {@link UsernamePasswordAuthenticationToken} for authentication.</p>
 *
 * <p>This class is active only when the "prod" profile is active.</p>
 *
 * @see AuthenticationProvider
 * @see UserDetailsService
 * @see PasswordEncoder
 */
@Component
@Profile("prod")
@RequiredArgsConstructor
public class HarmoniProdUserPasswordAuthenticationProvider implements AuthenticationProvider {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Authenticates the user based on the provided {@code authentication} object.
     *
     * <p>This method retrieves the user details from the {@link UserDetailsService} and creates an
     * {@link UsernamePasswordAuthenticationToken} with the user's authorities.</p>
     *
     * @param authentication the authentication request object.
     * @return authenticated {@link UsernamePasswordAuthenticationToken} if successful.
     * @throws AuthenticationException if authentication fails.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (passwordEncoder.matches(password, userDetails.getPassword())) {
            return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        } else {
            throw new BadCredentialsException("Nieprawidłowy login lub hasło");
        }
    }

    /**
     * Checks if this {@link AuthenticationProvider} supports the authentication token type.
     *
     * @param authentication the authentication token class to check.
     * @return {@code true} if the token class is supported, {@code false} otherwise.
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
