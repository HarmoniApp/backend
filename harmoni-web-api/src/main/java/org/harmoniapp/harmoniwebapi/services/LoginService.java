package org.harmoniapp.harmoniwebapi.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.LoginRequestDto;
import org.harmoniapp.harmoniwebapi.contracts.LoginResponseDto;
import org.harmoniapp.harmoniwebapi.exception.InactiveAccountException;
import org.harmoniapp.harmoniwebapi.exception.UnauthenticatedUserException;
import org.harmoniapp.harmoniwebapi.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    @Value("${server.servlet.context-path}")
    private String contextPath;

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
     * @throws InactiveAccountException if the user's account is inactive.
     * @throws UnauthenticatedUserException if the authentication fails due to invalid credentials.
     */
    public LoginResponseDto login(LoginRequestDto loginRequest) {
        //TODO: add calculation of invalid login attempts
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

            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("id", user.getId());

            boolean isOTP = LocalDateTime.now().isAfter(user.getLastPasswordChange());
            jwt= jwtTokenUtil.generateToken(authenticationResponse, extraClaims, isOTP);

            String path = null;
            if (isOTP) {
                path = String.format("%s/user/%d/changePassword", contextPath, user.getId());
            }

            return new LoginResponseDto(HttpStatus.OK.getReasonPhrase(), jwt, path);
        } else {
            throw new UnauthenticatedUserException("Invalid credentials");
        }
    }
}
