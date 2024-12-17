package org.harmoniapp.services.auth;

import org.harmoniapp.contracts.auth.LoginRequestDto;
import org.harmoniapp.contracts.auth.LoginResponseDto;

/**
 * Service interface for handling login operations.
 */
public interface LoginService {

    /**
     * Authenticates a user based on the provided login request.
     *
     * @param loginRequest the login request containing user credentials
     * @return the response containing authentication details
     */
    LoginResponseDto login(LoginRequestDto loginRequest);
}
