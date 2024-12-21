package org.harmoniapp.configuration;

import org.harmoniapp.repositories.user.UserRepository;
import org.harmoniapp.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Security configuration class for non-production environments.
 *
 * <p>This configuration is active only when the "prod" profile is not active.</p>
 */
@Configuration
@Profile("!prod")
public class ProjectSecurityConfig extends AbstractProjectSecurityConfig {

    @Autowired
    public ProjectSecurityConfig(AclConfig aclConfig, JwtTokenUtil jwtTokenUtil, HarmoniUserDetailsService harmoniUserDetailsService, UserRepository userRepository) {
        super(aclConfig, jwtTokenUtil, harmoniUserDetailsService, userRepository);
    }

    /**
     * Configures the security policy to specify that all requests should use insecure (non-HTTPS) channels.
     * <p>
     * This overrides the default behavior to explicitly allow requests over HTTP instead of HTTPS.
     * Use with caution, as insecure channels may expose data to potential interception.
     * </p>
     *
     * @param http The {@link HttpSecurity} object to configure.
     * @throws Exception If an error occurs during the configuration process.
     */
    @Override
    protected void configureRequiresChannel(HttpSecurity http) throws Exception {
        http.requiresChannel(rcc -> rcc.anyRequest().requiresInsecure());
    }

    /**
     * Configures and provides an {@link AuthenticationManager} using a custom
     * {@link HarmoniUserPasswordAuthenticationProvider}.
     *
     * @param userDetailsService the service for loading user details
     * @param passwordEncoder    the password encoder
     * @return the configured {@link AuthenticationManager}
     */
    @Override
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) {
        HarmoniUserPasswordAuthenticationProvider authenticationProvider =
                new HarmoniUserPasswordAuthenticationProvider(userDetailsService);
        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(false);
        return providerManager;
    }
}
