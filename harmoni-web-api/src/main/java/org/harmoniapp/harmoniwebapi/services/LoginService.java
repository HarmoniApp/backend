package org.harmoniapp.harmoniwebapi.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.LoginRequestDto;
import org.harmoniapp.harmoniwebapi.exception.InactiveAccountException;
import org.harmoniapp.harmoniwebapi.utils.JwtTokenUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service class responsible for handling user login logic and JWT token generation.
 *
 * @see RepositoryCollector
 * @see AuthenticationManager
 * @see JwtTokenUtil
 */
@Service
@RequiredArgsConstructor
public class LoginService {
    private final RepositoryCollector repositoryCollector;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * Authenticates the user and generates a JWT token upon successful login.
     * <p>
     * This method creates an authentication token based on the username and password provided in the {@link LoginRequestDto}.
     * If the authentication succeeds, a JWT token is generated with the user's ID included as an extra claim.
     * </p>
     *
     * @param loginRequest the login request containing the user's username and password.
     * @return the generated JWT token if authentication is successful.
     * @throws RuntimeException if the user is not found in the repository.
     */
    public String login(LoginRequestDto loginRequest) {
        String jwt = "";
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(),
                loginRequest.password());
        Authentication authenticationResponse = authenticationManager.authenticate(authentication);

        assert authenticationResponse != null;
        if (authenticationResponse.isAuthenticated()) {
            User user = repositoryCollector.getUsers().findByEmail(loginRequest.username()).orElse(null);
            if (user == null) {
                throw new RuntimeException("User not found!");
            }
            if (!user.isActive()) {
                throw new InactiveAccountException();
            }
            //TODO: Add a check to see if the password is a one-time use

            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("id", user.getId());

            jwt = jwtTokenUtil.generateToken(authenticationResponse, extraClaims);
        }
        return jwt;
    }
}
