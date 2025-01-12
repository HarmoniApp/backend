package org.harmoniapp.configuration;

import org.harmoniapp.filter.AuthoritiesLoggingAfterFilter;
import org.harmoniapp.filter.JWTTokenValidationFilter;
import org.harmoniapp.repositories.user.UserRepository;
import org.harmoniapp.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Profile("test")
@Configuration
public class ProjectSecurityTestConfig extends AbstractProjectSecurityConfig{

    @Autowired
    public ProjectSecurityTestConfig(AclConfig aclConfig, JwtTokenUtil jwtTokenUtil, HarmoniUserDetailsService harmoniUserDetailsService, UserRepository userRepository) {
        super(aclConfig, jwtTokenUtil, harmoniUserDetailsService, userRepository);
    }

    /**
     * Configures Cross-Site Request Forgery (CSRF) protection settings.
     *
     * @param http the HttpSecurity object to configure
     * @throws Exception if an error occurs while configuring security settings
     */
    @Override
    protected void configureCsrf(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .addFilterAfter(new AuthoritiesLoggingAfterFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new JWTTokenValidationFilter(jwtTokenUtil, harmoniUserDetailsService, userRepository), BasicAuthenticationFilter.class);
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
