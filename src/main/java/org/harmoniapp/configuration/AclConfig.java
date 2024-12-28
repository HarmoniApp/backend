package org.harmoniapp.configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * Interface for configuring ACL (Access Control List) settings.
 */
public interface AclConfig {

    /**
     * Configures the HTTP security settings.
     *
     * @param http the HttpSecurity to configure
     * @throws Exception if an error occurs during configuration
     */
    void configure(HttpSecurity http) throws Exception;
}
