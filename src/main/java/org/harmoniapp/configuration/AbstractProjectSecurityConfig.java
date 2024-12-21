package org.harmoniapp.configuration;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.exceptionhandling.CustomAccessDeniedHandler;
import org.harmoniapp.exceptionhandling.CustomBasicAuthenticationEntryPoint;
import org.harmoniapp.filter.AuthoritiesLoggingAfterFilter;
import org.harmoniapp.filter.CsrfCookieFilter;
import org.harmoniapp.filter.JWTTokenValidationFilter;
import org.harmoniapp.repositories.user.UserRepository;
import org.harmoniapp.utils.JwtTokenUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;
import java.util.List;

/**
 * Abstract base class for project security configuration.
 * Provides common security configurations and abstract methods for specific configurations.
 */
@Configuration
@RequiredArgsConstructor
public abstract class AbstractProjectSecurityConfig {
    protected final AclConfig aclConfig;
    protected final JwtTokenUtil jwtTokenUtil;
    protected final HarmoniUserDetailsService harmoniUserDetailsService;
    protected final UserRepository userRepository;

    /**
     * Configures the default security filter chain.
     *
     * @param http the HttpSecurity object to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs while configuring security settings
     */
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        configureSessionManagement(http);
        configureCors(http);
        configureCsrf(http);
        configureRequiresChannel(http);
        configureAcl(http);
        configureEntryPoint(http);
        configureExceptionHandling(http);
        return http.build();
    }

    /**
     * Configures session management to use stateless sessions.
     *
     * @param http the HttpSecurity object to configure
     * @throws Exception if an error occurs while configuring security settings
     */
    protected void configureSessionManagement(HttpSecurity http) throws Exception {
        http.sessionManagement(sessionConfig ->
                sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    }

    /**
     * Configures Cross-Origin Resource Sharing (CORS) settings.
     *
     * @param http the HttpSecurity object to configure
     * @throws Exception if an error occurs while configuring security settings
     */
    protected void configureCors(HttpSecurity http) throws Exception {
        http.cors(corsConfig -> corsConfig.configurationSource(new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                config.setAllowedMethods(Collections.singletonList("*"));
                config.setAllowCredentials(true);
                config.setAllowedHeaders(Collections.singletonList("*"));
                config.setExposedHeaders(List.of("Authorization"));
                config.setMaxAge(3600L);
                return config;
            }
        }));
    }

    /**
     * Configures Cross-Site Request Forgery (CSRF) protection settings.
     *
     * @param http the HttpSecurity object to configure
     * @throws Exception if an error occurs while configuring security settings
     */
    protected void configureCsrf(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler csrfTokenRequestHandler = new CsrfTokenRequestAttributeHandler();

        http.csrf(csrfConfig -> csrfConfig.csrfTokenRequestHandler(csrfTokenRequestHandler)
                        .ignoringRequestMatchers("/login") //public endpoints
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(new AuthoritiesLoggingAfterFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new JWTTokenValidationFilter(jwtTokenUtil, harmoniUserDetailsService, userRepository), BasicAuthenticationFilter.class);
    }

    /**
     * Abstract method to configure channel security settings.
     *
     * @param http the HttpSecurity object to configure
     * @throws Exception if an error occurs while configuring security settings
     */
    protected abstract void configureRequiresChannel(HttpSecurity http) throws Exception;

    /**
     * Configures Access Control List (ACL) settings.
     *
     * @param http the HttpSecurity object to configure
     * @throws Exception if an error occurs while configuring security settings
     */
    protected void configureAcl(HttpSecurity http) throws Exception {
        aclConfig.configure(http);
    }

    /**
     * Configures the entry point for basic authentication.
     *
     * @param http the HttpSecurity object to configure
     * @throws Exception if an error occurs while configuring security settings
     */
    protected void configureEntryPoint(HttpSecurity http) throws Exception {
        http.httpBasic(hbc ->
                hbc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint()));
    }

    /**
     * Configures exception handling settings.
     *
     * @param http the HttpSecurity object to configure
     * @throws Exception if an error occurs while configuring security settings
     */
    protected void configureExceptionHandling(HttpSecurity http) throws Exception {
        http.exceptionHandling(ehc ->
                ehc.accessDeniedHandler(new CustomAccessDeniedHandler()));
    }

    /**
     * Provides a default {@link PasswordEncoder} bean for encoding passwords.
     *
     * @return the password encoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * Provides a {@link CompromisedPasswordChecker} bean for checking compromised passwords.
     *
     * @return the compromised password checker.
     */
    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker() {
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }

    /**
     * Abstract method to provide an {@link AuthenticationManager} bean.
     *
     * @param userDetailsService the service for loading user details
     * @param passwordEncoder    the password encoder
     * @return the configured {@link AuthenticationManager}
     */
    @Bean
    public abstract AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder);
}
