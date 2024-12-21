package org.harmoniapp.controllers.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to handle CSRF token requests.
 */
@RestController
@RequiredArgsConstructor
public class CsrfController {

    /**
     * Endpoint to retrieve the CSRF token.
     *
     * @param token the CSRF token
     * @return the CSRF token
     */
    @GetMapping("/csrf")
    public CsrfToken getCsrfToken(CsrfToken token) {
        return token;
    }
}
