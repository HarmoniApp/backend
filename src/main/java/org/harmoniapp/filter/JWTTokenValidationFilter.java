package org.harmoniapp.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.exception.EntityNotFound;
import org.harmoniapp.repositories.user.UserRepository;
import org.harmoniapp.configuration.HarmoniUserDetailsService;
import org.harmoniapp.configuration.Principle;
import org.harmoniapp.utils.JwtTokenUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter class for validating JWT tokens.
 * <p>
 * This filter class is used to validate JWT tokens received in the request headers. It extracts the token from the
 * request header, validates the token, and sets the user authentication details in the security context.
 * </p>
 */
@RequiredArgsConstructor
@Slf4j
public class JWTTokenValidationFilter extends OncePerRequestFilter {
    private final JwtTokenUtil jwtTokenUtil;
    private final HarmoniUserDetailsService userDetailsService;
    private final UserRepository userRepository;

    /**
     * Filters incoming requests to validate and authenticate JWT tokens.
     *
     * @param request the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @param filterChain the FilterChain object
     * @throws ServletException if an error occurs during filtering
     * @throws IOException if an I/O error occurs during filtering
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = extractJwtFromRequest(request);

        try {
            validateAndAuthenticateJwt(jwt);
        } catch (Exception e) {
            throw new BadCredentialsException("Nie prawidłowy token");
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Determines whether this filter should not apply to a particular request.
     * This filter is skipped for the "/login" endpoint.
     *
     * @param request the {@link HttpServletRequest} object
     * @return {@code true} if the filter should not be applied to this request
     * @throws ServletException if an error occurs during filtering
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getServletPath().equals("/login") //login path
                || request.getServletPath().startsWith("/ws/"); //websocket path
    }

    /**
     * Validates and authenticates the provided JWT token.
     *
     * @param jwt the JWT token to validate and authenticate
     * @throws BadCredentialsException if the token is invalid or the user is not found
     */
    private void validateAndAuthenticateJwt(String jwt) {
        String username = jwtTokenUtil.getUsername(jwt);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new BadCredentialsException("Nie prawidłowy email lub hasło"));

        if (jwtTokenUtil.isTokenValid(jwt, userDetails)) {
            setAuthenticationContext(jwt, userDetails, username);
        }
    }

    /**
     * Extracts the JWT token from the request header.
     *
     * @param request the HttpServletRequest object
     * @return the extracted JWT token
     * @throws BadCredentialsException if the token is missing or does not start with "Bearer "
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String jwt = request.getHeader(jwtTokenUtil.getAUTH_HEADER());
        if (jwt == null || !StringUtils.startsWithIgnoreCase(jwt, "Bearer ")) {
            throw new BadCredentialsException("Nie prawidłowy token");
        }
        return jwt.substring(7);
    }

    /**
     * Sets the authentication context for the current request.
     *
     * @param jwt the JWT token
     * @param userDetails the user details
     * @param username the username
     */
    private void setAuthenticationContext(String jwt, UserDetails userDetails, String username) {
        Long id = jwtTokenUtil.getUserId(jwt);
        Principle principle = new Principle(id, username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principle, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}