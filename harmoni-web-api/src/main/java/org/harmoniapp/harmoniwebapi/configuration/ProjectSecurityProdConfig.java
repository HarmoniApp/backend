package org.harmoniapp.harmoniwebapi.configuration;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.repositories.UserRepository;
import org.harmoniapp.harmoniwebapi.exceptionhandling.CustomAccessDeniedHandler;
import org.harmoniapp.harmoniwebapi.exceptionhandling.CustomBasicAuthenticationEntryPoint;
import org.harmoniapp.harmoniwebapi.filter.AuthoritiesLoggingAfterFilter;
import org.harmoniapp.harmoniwebapi.filter.CsrfCookieFilter;
import org.harmoniapp.harmoniwebapi.filter.JWTTokenValidationFilter;
import org.harmoniapp.harmoniwebapi.utils.JwtTokenUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

/**
 * Security configuration class for production environments.
 *
 * <p>This configuration is active only when the "prod" profile is active.</p>
 *
 * @see AuthorizationManager
 * @see JwtTokenUtil
 * @see HarmoniUserDetailsService
 * @see SecurityFilterChain
 */
@Configuration
@Profile("prod")
@RequiredArgsConstructor
public class ProjectSecurityProdConfig {
    private final AuthorizationManager<RequestAuthorizationContext> adminOrOwnerAuthorizationManager;
    private final AuthorizationManager<RequestAuthorizationContext> ownerAuthorizationManager;
    private final AuthorizationManager<RequestAuthorizationContext> adminOrOwnerQueryParamAuthorizationManager;
    private final AuthorizationManager<RequestAuthorizationContext> ownerQueryParamAuthorizationManager;
    private final AuthorizationManager<RequestAuthorizationContext> groupMemberAuthorizationManager;
    private final AuthorizationManager<RequestAuthorizationContext> conversationMemberQueryParamAuthorizationManager;

    private final JwtTokenUtil jwtTokenUtil;
    private final HarmoniUserDetailsService harmoniUserDetailsService;
    private final UserRepository userRepository;

    /**
     * Configures the security filter chain for HTTP requests.
     *
     * <p>This includes session management, CORS configuration, CSRF protection, JWT token filters,
     * and request authorization based on user roles and custom authorization managers.
     * It also forces all requests to use HTTPS and configures which requests are allowed or require authentication.</p>
     *
     * <p>Endpoints like "/login" and "/error" are publicly accessible, while other endpoints are restricted
     * based on roles and custom access rules, such as admin or owner checks.</p>
     *
     * @param http the {@link HttpSecurity} to configure.
     * @return the configured {@link SecurityFilterChain}.
     * @throws Exception if there is a problem configuring the filter chain.
     */
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler csrfTokenRequestHandler = new CsrfTokenRequestAttributeHandler();

        http.sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.cors(corsConfig -> corsConfig.configurationSource(new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                config.setAllowedMethods(Collections.singletonList("*"));
                config.setAllowCredentials(true);
                config.setAllowedHeaders(Collections.singletonList("*"));
                config.setExposedHeaders(Arrays.asList("Authorization"));
                config.setMaxAge(3600L);
                return config;
            }
        }));

        http.csrf(csrfConfig -> csrfConfig.csrfTokenRequestHandler(csrfTokenRequestHandler)
                        .ignoringRequestMatchers("/login") //public endpoints
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(new AuthoritiesLoggingAfterFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new JWTTokenValidationFilter(jwtTokenUtil, harmoniUserDetailsService, userRepository), BasicAuthenticationFilter.class);

//        http.requiresChannel(rcc -> rcc.anyRequest().requiresInsecure()); //Only HTTP
        http.requiresChannel(rcc -> rcc.anyRequest().requiresSecure()); //Only HTTPS

        http.authorizeHttpRequests(request -> request.requestMatchers("/login", "/error", "/ws/**").permitAll()
                        .requestMatchers("/csrf").authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/absence/{id}/status/{statusId}", "DELETE")).access(adminOrOwnerAuthorizationManager)
                        .requestMatchers(new AntPathRequestMatcher("/absence", "POST"),
                                new AntPathRequestMatcher("/absence/{id}", "PUT"),
                                new AntPathRequestMatcher("/absence/archive/{id}", "PATCH")).hasRole("USER")
                        .requestMatchers("/absence/user/{id}/**").access(adminOrOwnerAuthorizationManager)
                        .requestMatchers("/absence/range/**").authenticated()
                        .requestMatchers("/absence/**").hasRole("ADMIN")
                        .requestMatchers("/absence-type/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/address/**").hasRole("ADMIN")
                        .requestMatchers("/archived-shifts/**").hasRole("ADMIN")
                        .requestMatchers("/aiSchedule/**").hasRole("ADMIN")
                        .requestMatchers("/contract-type/**").hasRole("ADMIN")
                        .requestMatchers("/excel/**").hasRole("ADMIN")
                        .requestMatchers("/language/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/notification/user/{id}/**").access(ownerAuthorizationManager)
                        .requestMatchers("/notification/**").authenticated()
                        .requestMatchers("/pdf/**").hasRole("ADMIN")
                        .requestMatchers("/predefine-shift/**").hasRole("ADMIN")
                        .requestMatchers("/role/user/{id}/**").access(adminOrOwnerAuthorizationManager)
                        .requestMatchers("/role/**").hasRole("ADMIN")
                        .requestMatchers("/shift/range").access(adminOrOwnerQueryParamAuthorizationManager)
                        .requestMatchers(new AntPathRequestMatcher("/shift/{id}", "GET")).authenticated()
                        .requestMatchers("/shift/**").hasRole("ADMIN")
                        .requestMatchers("/status").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/user/simple/empId/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/user/simple/**",
                                "/user/supervisor",
                                "/user/search").hasRole("ADMIN")
                        .requestMatchers("/user/{id}/photo").authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/user/{id}/changePassword"),
                                new AntPathRequestMatcher("/user/{id}/uploadPhoto"),
                                new AntPathRequestMatcher("/user/{id}/defaultPhoto")).access(ownerAuthorizationManager)
                        .requestMatchers(new AntPathRequestMatcher("/user/{id}/**", "GET")).access(adminOrOwnerAuthorizationManager)
                        .requestMatchers("/user/**").hasRole("ADMIN")
                        .requestMatchers("/calendar/user/{id}/**").access(adminOrOwnerAuthorizationManager)
                        .requestMatchers("/userPhoto/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/group/chat-partners/**").access(ownerQueryParamAuthorizationManager)
                        .requestMatchers(new AntPathRequestMatcher("/group", "POST")).hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/group/details/{groupId}/**",
                                "/group/{groupId}/**").access(groupMemberAuthorizationManager)
                        .requestMatchers("/group/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/message/history/**",
                                "/message/last/**").access(conversationMemberQueryParamAuthorizationManager)
//                        .requestMatchers("/message/chat-partners/**").access(ownerQueryParamAuthorizationManager)
                        .requestMatchers("/message/**").hasAnyRole("USER", "ADMIN")
                )
                .httpBasic(hbc -> hbc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint()))
                .exceptionHandling(ehc -> ehc.accessDeniedHandler(new CustomAccessDeniedHandler()));

        return http.build();
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
     * Configures and provides an {@link AuthenticationManager} using a custom
     * {@link HarmoniProdUserPasswordAuthenticationProvider}.
     *
     * @param userDetailsService the service for loading user details.
     * @param passwordEncoder    the encoder for checking user passwords.
     * @return the configured {@link AuthenticationManager}.
     */
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
