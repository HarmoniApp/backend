package org.harmoniapp.harmoniwebapi.configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.harmoniapp.harmoniwebapi.filter.CsrfCookieFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class ProjectSecurityConfig {

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler csrfTokenRequestHandler = new CsrfTokenRequestAttributeHandler();

        http.requiresChannel(rcc -> rcc.anyRequest().requiresInsecure()); //Only HTTP
//        http.requiresChannel(rcc -> rcc.anyRequest().requiresSecure()); //Only HTTPS

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
//                        .ignoringRequestMatchers("/pub") //public endpoints
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class);

        http.authorizeHttpRequests(request -> request
                        .requestMatchers(new AntPathRequestMatcher("/absence", "POST"),
                                new AntPathRequestMatcher("/absence/{id}", "PUT"),
                                new AntPathRequestMatcher("/absence/archive/{id}", "PATCH")).hasRole("USER")
                        .requestMatchers("/absence/**").hasRole("ADMIN")
                        .requestMatchers("/absence-type/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/address/**").hasRole("ADMIN")
                        .requestMatchers("/archived-shifts/**").hasRole("ADMIN")
                        .requestMatchers("/contract-type/**").hasRole("ADMIN")
                        .requestMatchers("/language/**").hasRole("ADMIN")
                        .requestMatchers("/notification/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/predefine-shift/**").hasRole("ADMIN")
                        .requestMatchers("/role/**").hasRole("ADMIN") // role/user/{id} only for admin?
                        .requestMatchers("/shift/{id}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/shift/range").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/shift/**").hasRole("ADMIN")
                        .requestMatchers("/status").hasAnyRole("USER", "ADMIN")
//                        .requestMatchers("/user/simple/**").hasRole("ADMIN")
                        .requestMatchers("/user/supervisor").hasRole("ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/user/{id}", "PATCH"),
                                new AntPathRequestMatcher("/user/{id}", "DELETE")).hasRole("ADMIN")
                        .requestMatchers("/user/{id}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/user/**").hasRole("ADMIN")
                        .requestMatchers("/calendar/**").hasAnyRole("USER", "ADMIN")
                )
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());


        return http.build();
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("password")
//                .roles("USER")
//                .build();
//
//        UserDetails admin = User.withDefaultPasswordEncoder()
//                .username("admin")
//                .password("password")
//                .roles("ADMIN")
//                .build();
//
//
//        return new InMemoryUserDetailsManager(user, admin);
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
