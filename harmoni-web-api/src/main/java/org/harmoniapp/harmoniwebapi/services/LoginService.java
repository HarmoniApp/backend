package org.harmoniapp.harmoniwebapi.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.LoginRequestDto;
import org.harmoniapp.harmoniwebapi.contracts.LoginResponseDto;
import org.harmoniapp.harmoniwebapi.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
     * If the user's password has expired, an OTP flag is set and a path for changing the password is included in the response.
     * </p>
     *
     * @param loginRequest the login request containing the user's username and password.
     * @return the generated JWT token if authentication is successful, along with the HTTP status and optional path for password change.
     * @throws AccessDeniedException if the authentication fails due to invalid credentials.
     */
    public LoginResponseDto login(LoginRequestDto loginRequest) {
        String jwt = "";
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(),
                loginRequest.password());
        Authentication authenticationResponse = null;
        User user = repositoryCollector.getUsers().findByEmail(loginRequest.username()).orElse(null);
        if (user == null) {
            throw new AccessDeniedException("Invalid credentials");
        }

        try {
            authenticationResponse = authenticationManager.authenticate(authentication);
        } catch (BadCredentialsException e) {


            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            repositoryCollector.getUsers().save(user);
        }

        if (authenticationResponse == null || !authenticationResponse.isAuthenticated()) {
            throw new AccessDeniedException("Invalid credentials");
        }


        user.setFailedLoginAttempts(0);
        repositoryCollector.getUsers().save(user);

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("id", user.getId());

        boolean isOTP = LocalDate.now().isAfter(user.getPasswordExpirationDate());
        jwt = jwtTokenUtil.generateToken(authenticationResponse, extraClaims, isOTP);

        String path = null;
        if (isOTP) {
            path = String.format("%s/user/%d/changePassword", contextPath, user.getId());
        }

        return new LoginResponseDto(HttpStatus.OK.getReasonPhrase(), jwt, path);
    }
}
