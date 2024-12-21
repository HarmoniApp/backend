package org.harmoniapp.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

/**
 * Configuration class for setting up Access Control List (ACL) rules.
 * This class configures the security settings for various endpoints in the application.
 */
@Component
@RequiredArgsConstructor
public class AclConfigImpl implements AclConfig {
    protected final AuthorizationManager<RequestAuthorizationContext> adminOrOwnerAuthorizationManager;
    protected final AuthorizationManager<RequestAuthorizationContext> ownerAuthorizationManager;
    protected final AuthorizationManager<RequestAuthorizationContext> adminOrOwnerQueryParamAuthorizationManager;
    protected final AuthorizationManager<RequestAuthorizationContext> ownerQueryParamAuthorizationManager;
    protected final AuthorizationManager<RequestAuthorizationContext> groupMemberAuthorizationManager;
    protected final AuthorizationManager<RequestAuthorizationContext> conversationMemberQueryParamAuthorizationManager;

    /**
     * Configures the security settings for the application.
     *
     * @param http the HttpSecurity object to configure
     * @throws Exception if an error occurs while configuring security settings
     */
    public void configure(HttpSecurity http) throws Exception {
        configurePublicEndpoints(http);
        configureAbsenceEndpoints(http);
        configureChatEndpoints(http);
        configureImportExportEndpoints(http);
        configureNotificationEndpoints(http);
        configureProfileEndpoints(http);
        configureScheduleEndpoints(http);
        configureUserEndpoints(http);
    }

    /**
     * Configures the security settings for public endpoints.
     *
     * @param http the HttpSecurity object to configure
     * @throws Exception if an error occurs while configuring security settings
     */
    private void configurePublicEndpoints(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request -> request
                .requestMatchers("/login", "/error", "/ws/**").permitAll()
                .requestMatchers("/csrf").authenticated());
    }

    /**
     * Configures the security settings for absence-related endpoints.
     *
     * @param http the HttpSecurity object to configure
     * @throws Exception if an error occurs while configuring security settings
     */
    private void configureAbsenceEndpoints(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request -> request
                .requestMatchers(new AntPathRequestMatcher("/absence/{id}/status/{statusId}", "DELETE")).authenticated()
                .requestMatchers(new AntPathRequestMatcher("/absence", "POST"),
                        new AntPathRequestMatcher("/absence/{id}", "PUT")).hasRole("USER")
                .requestMatchers("/absence/user/{id}/**").access(adminOrOwnerAuthorizationManager)
                .requestMatchers("/absence/**").hasRole("ADMIN")
                .requestMatchers("/absence-type/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/status").hasAnyRole("USER", "ADMIN"));
    }

    /**
     * Configures the security settings for chat-related endpoints.
     *
     * @param http the HttpSecurity object to configure
     * @throws Exception if an error occurs while configuring security settings
     */
    private void configureChatEndpoints(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request -> request
                .requestMatchers("/group/chat-partners/**").access(ownerQueryParamAuthorizationManager)
                .requestMatchers(new AntPathRequestMatcher("/group", "POST")).hasAnyRole("USER", "ADMIN")
                .requestMatchers("/group/details/{groupId}/**","/group/{groupId}/**").access(groupMemberAuthorizationManager)
                .requestMatchers("/group/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/message/history/**", "/message/last/**").access(conversationMemberQueryParamAuthorizationManager)
                .requestMatchers("/message/**").hasAnyRole("USER", "ADMIN"));
    }

    /**
     * Configures the security settings for import/export-related endpoints.
     *
     * @param http the HttpSecurity object to configure
     * @throws Exception if an error occurs while configuring security settings
     */
    private void configureImportExportEndpoints(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request -> request
                .requestMatchers("/excel/**").hasRole("ADMIN")
                .requestMatchers("/pdf/**").hasRole("ADMIN"));
    }

    /**
     * Configures the security settings for notification-related endpoints.
     *
     * @param http the HttpSecurity object to configure
     * @throws Exception if an error occurs while configuring security settings
     */
    private void configureNotificationEndpoints(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request -> request
                .requestMatchers("/notification/user/{id}/**").access(ownerAuthorizationManager)
                .requestMatchers("/notification/**").authenticated());
    }

    /**
     * Configures the security settings for profile-related endpoints.
     *
     * @param http the HttpSecurity object to configure
     * @throws Exception if an error occurs while configuring security settings
     */
    private void configureProfileEndpoints(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request -> request
                .requestMatchers("/address/**").hasRole("ADMIN")
                .requestMatchers("/contract-type/**").hasRole("ADMIN")
                .requestMatchers("/language/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/role/user/{id}/**").access(adminOrOwnerAuthorizationManager)
                .requestMatchers("/role/**").hasRole("ADMIN"));
    }

    /**
     * Configures the security settings for schedule-related endpoints.
     *
     * @param http the HttpSecurity object to configure
     * @throws Exception if an error occurs while configuring security settings
     */
    private void configureScheduleEndpoints(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request -> request
                .requestMatchers(new AntPathRequestMatcher("/shift/{id}", "GET")).authenticated()
                .requestMatchers("/shift/range").access(adminOrOwnerQueryParamAuthorizationManager)
                .requestMatchers("/shift/**").hasRole("ADMIN")
                .requestMatchers("/aiSchedule/**").hasRole("ADMIN")
                .requestMatchers("/predefine-shift/**").hasRole("ADMIN")
                .requestMatchers("/calendar/user/{id}/**").access(adminOrOwnerAuthorizationManager));
    }

    /**
     * Configures the security settings for user-related endpoints.
     *
     * @param http the HttpSecurity object to configure
     * @throws Exception if an error occurs while configuring security settings
     */
    private void configureUserEndpoints(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request -> request
                .requestMatchers("/user/simple/empId/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/user/simple/**", "/user/supervisor", "/user/search").hasRole("ADMIN")
                .requestMatchers("/user/{id}/photo").authenticated()
                .requestMatchers(new AntPathRequestMatcher("/user/{id}/changePassword"),
                        new AntPathRequestMatcher("/user/{id}/uploadPhoto"),
                        new AntPathRequestMatcher("/user/{id}/defaultPhoto")).access(ownerAuthorizationManager)
                .requestMatchers(new AntPathRequestMatcher("/user/{id}/**", "GET")).access(adminOrOwnerAuthorizationManager)
                .requestMatchers("/user/**").hasRole("ADMIN")
                .requestMatchers("/userPhoto/**").hasAnyRole("USER", "ADMIN"));
    }
}
