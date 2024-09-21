package org.harmoniapp.harmoniwebapi.configuration;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.exceptionhandling.CustomAccessDeniedHandler;
import org.harmoniapp.harmoniwebapi.exceptionhandling.CustomBasicAuthenticationEntryPoint;
import org.harmoniapp.harmoniwebapi.filter.*;
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

@Configuration
@Profile("prod")
@RequiredArgsConstructor
public class ProjectSecurityProdConfig {

    private final AuthorizationManager<RequestAuthorizationContext> adminOrOwnerAuthorizationManager;
    private final AuthorizationManager<RequestAuthorizationContext> ownerAuthorizationManager;
    private final AuthorizationManager<RequestAuthorizationContext> adminOrOwnerQueryParamAuthorizationManager;

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
                .addFilterAfter(new JWTTokenGeneratorFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new JWTTokenValidationFilter(), BasicAuthenticationFilter.class);

//        http.requiresChannel(rcc -> rcc.anyRequest().requiresInsecure()); //Only HTTP
        http.requiresChannel(rcc -> rcc.anyRequest().requiresSecure()); //Only HTTPS

        http.authorizeHttpRequests(request -> request.requestMatchers("/login", "/error").permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/absence", "POST"),
                                new AntPathRequestMatcher("/absence/{id}", "PUT"),
                                new AntPathRequestMatcher("/absence/archive/{id}", "PATCH")).hasRole("USER")
                        .requestMatchers("/absence/user/{id}/**").access(adminOrOwnerAuthorizationManager)
                        .requestMatchers("/absence/**").hasRole("ADMIN")
                        .requestMatchers("/absence-type/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/address/**").hasRole("ADMIN")
                        .requestMatchers("/archived-shifts/**").hasRole("ADMIN")
                        .requestMatchers("/contract-type/**").hasRole("ADMIN")
                        .requestMatchers("/language/**").hasRole("ADMIN")
                        .requestMatchers("/notification/user/{id}/**").access(ownerAuthorizationManager)
                        .requestMatchers("/notification/**").authenticated()
                        .requestMatchers("/predefine-shift/**").hasRole("ADMIN")
                        .requestMatchers("/role/**").hasRole("ADMIN") // role/user/{id} only for admin?
                        .requestMatchers("/shift/range").access(adminOrOwnerQueryParamAuthorizationManager)
                        .requestMatchers(new AntPathRequestMatcher("/shift/{id}", "GET")).authenticated()
                        .requestMatchers("/shift/**").hasRole("ADMIN")
                        .requestMatchers("/status").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/user/simple/**",
                                "/user/supervisor",
                                "/user/search").hasRole("ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/user/{id}/changePassword")).access(ownerAuthorizationManager)
                        .requestMatchers(new AntPathRequestMatcher("/user/{id}", "GET")).access(adminOrOwnerAuthorizationManager)
                        .requestMatchers("/user/**").hasRole("ADMIN")
                        .requestMatchers("/calendar/user/{id}/**").access(adminOrOwnerAuthorizationManager)
                )
                .httpBasic(hbc -> hbc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint()))
                .exceptionHandling(ehc -> ehc.accessDeniedHandler(new CustomAccessDeniedHandler()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker() {
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }

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
