package org.harmoniapp.harmoniwebapi.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

/**
 * Authentication provider for user password authentication in non-production environments.
 * This provider skips certain security checks and returns a token for any valid user.
 *
 * <p>It supports {@link UsernamePasswordAuthenticationToken} for authentication.</p>
 *
 * <p>This class is active only when the "prod" profile is not active.</p>
 *
 * @see AuthenticationProvider
 * @see UserDetailsService
 */
@Component
@Profile("!prod")
@RequiredArgsConstructor
public class HarmoniUserPasswordAuthenticationProvider implements AuthenticationProvider {
    private final UserDetailsService userDetailsService;

    /**
     * Authenticates the user based on the provided {@code authentication} object.
     *
     * <p>This method retrieves the user details from the {@link UserDetailsService} and creates an
     * {@link UsernamePasswordAuthenticationToken} with the user's authorities without checking the password.</p>
     *
     * @param authentication the authentication request object.
     * @return authenticated {@link UsernamePasswordAuthenticationToken}.
     * @throws AuthenticationException if authentication fails.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
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
