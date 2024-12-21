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
 * Security configuration class for production environments.
 *
 * <p>This configuration is active only when the "prod" profile is active.</p>
 */
@Configuration
@Profile("prod")
public class ProjectSecurityProdConfig extends AbstractProjectSecurityConfig {
    @Autowired
    public ProjectSecurityProdConfig(AclConfig aclConfig, JwtTokenUtil jwtTokenUtil, HarmoniUserDetailsService harmoniUserDetailsService, UserRepository userRepository) {
        super(aclConfig, jwtTokenUtil, harmoniUserDetailsService, userRepository);
    }

    /**
     * Configures the security policy to enforce that all requests must use secure (HTTPS) channels.
     * <p>
     * This ensures that all incoming requests are redirected to HTTPS if they are made over HTTP,
     * enhancing security by encrypting the communication between the client and server.
     * </p>
     *
     * @param http The {@link HttpSecurity} object to configure.
     * @throws Exception If an error occurs during the configuration process.
     */
    @Override
    protected void configureRequiresChannel(HttpSecurity http) throws Exception {
        http.requiresChannel(rcc -> rcc.anyRequest().requiresSecure());
    }

    /**
     * Configures and provides an {@link AuthenticationManager} using a custom
     * {@link HarmoniProdUserPasswordAuthenticationProvider}.
     *
     * @param userDetailsService the service for loading user details.
     * @param passwordEncoder    the encoder for checking user passwords.
     * @return the configured {@link AuthenticationManager}.
     */
    @Override
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) {
        HarmoniProdUserPasswordAuthenticationProvider authenticationProvider =
                new HarmoniProdUserPasswordAuthenticationProvider(userDetailsService, passwordEncoder);
        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(false);
        return providerManager;
    }
}
