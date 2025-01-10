package org.harmoniapp.services.auth;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.auth.LoginRequestDto;
import org.harmoniapp.contracts.auth.LoginResponseDto;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.utils.JwtTokenUtil;
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
 * Service implementation for handling user login operations.
 */
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    private final RepositoryCollector repositoryCollector;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param loginRequest the login request containing username and password
     * @return a LoginResponseDto containing the status, JWT token, and optional password change path
     */
    @Override
    public LoginResponseDto login(LoginRequestDto loginRequest) {
        User user = findUserByEmail(loginRequest.username());
        Authentication authenticationResponse = authenticateUser(loginRequest, user);
        resetFailedLoginAttempts(user);

        Map<String, Object> extraClaims = createExtraClaims(user);
        boolean isOTP = isPasswordExpired(user);
        String jwt = jwtTokenUtil.generateToken(authenticationResponse, extraClaims, isOTP);

        String path = isOTP ? generatePasswordChangePath(user) : null;
        return new LoginResponseDto(HttpStatus.OK.getReasonPhrase(), jwt, path);
    }

    /**
     * Finds a user by their email address.
     *
     * @param email the email address of the user
     * @return the User object if found
     * @throws AccessDeniedException if the user is not found
     */
    private User findUserByEmail(String email) {
        return repositoryCollector.getUsers().findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("Nieprawidłowy email lub hasło"));
    }

    /**
     * Authenticates the user using the provided login request and user details.
     *
     * @param loginRequest the login request containing username and password
     * @param user         the user details retrieved from the database
     * @return the authenticated user details
     * @throws AccessDeniedException if the authentication fails due to invalid credentials
     */
    private Authentication authenticateUser(LoginRequestDto loginRequest, User user) {
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(
                loginRequest.username(), loginRequest.password());
        try {
            return authenticationManager.authenticate(authentication);
        } catch (BadCredentialsException e) {
            incrementFailedLoginAttempts(user);
            throw new AccessDeniedException("Nieprawidłowy email lub hasło");
        }
    }

    /**
     * Increments the failed login attempts counter for the given user.
     *
     * @param user the user whose failed login attempts counter is to be incremented
     */
    private void incrementFailedLoginAttempts(User user) {
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        repositoryCollector.getUsers().save(user);
    }

    /**
     * Resets the failed login attempts counter for the given user.
     *
     * @param user the user whose failed login attempts counter is to be reset
     */
    private void resetFailedLoginAttempts(User user) {
        user.setFailedLoginAttempts(0);
        repositoryCollector.getUsers().save(user);
    }

    /**
     * Creates a map of extra claims to be included in the JWT token.
     *
     * @param user the user for whom the extra claims are being created
     * @return a map containing the extra claims
     */
    private Map<String, Object> createExtraClaims(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("id", user.getId());
        return extraClaims;
    }

    /**
     * Checks if the user's password has expired.
     *
     * @param user the user whose password expiration date is to be checked
     * @return true if the password is expired, false otherwise
     */
    private boolean isPasswordExpired(User user) {
        return LocalDate.now().isAfter(user.getPasswordExpirationDate());
    }

    /**
     * Generates the password change path for the given user.
     *
     * @param user the user for whom the password change path is being generated
     * @return the password change path as a String
     */
    private String generatePasswordChangePath(User user) {
        return String.format("%s/user/%d/changePassword", contextPath, user.getId());
    }
}
