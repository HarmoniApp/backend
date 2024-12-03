package org.harmoniapp.harmoniwebapi.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.repositories.UserRepository;
import org.harmoniapp.harmoniwebapi.configuration.HarmoniUserDetailsService;
import org.harmoniapp.harmoniwebapi.configuration.Principle;
import org.harmoniapp.harmoniwebapi.utils.JwtTokenUtil;
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
public class JWTTokenValidationFilter extends OncePerRequestFilter {
    private final JwtTokenUtil jwtTokenUtil;
    private final HarmoniUserDetailsService userDetailsService;
    private final UserRepository userRepository;

    /**
     * Validates the JWT token received in the request header.
     * <p>
     * This method extracts the JWT token from the request header, validates the token, and sets the user authentication
     * details in the security context.
     * </p>
     *
     * @param request     the {@link HttpServletRequest} object containing the request details.
     * @param response    the {@link HttpServletResponse} object containing the response details.
     * @param filterChain the {@link FilterChain} object containing the filter chain.
     * @throws ServletException if an error occurs during the filter process.
     * @throws IOException      if an error occurs during the filter process.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = request.getHeader(jwtTokenUtil.getAUTH_HEADER());
        String username = null;

        assert jwt != null;
        if (!StringUtils.startsWithIgnoreCase(jwt, "Bearer ")) {
            throw new BadCredentialsException("Invalid Token received");
        }
        jwt = jwt.substring(7);
        try {
            username = jwtTokenUtil.getUsername(jwt);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            User user = userRepository.findByEmail(username).orElseThrow(IllegalArgumentException::new);

            if (jwtTokenUtil.isTokenValid(jwt, userDetails)) {
                Long id = jwtTokenUtil.getUserId(jwt);
                Principle principle = new Principle(id, username);
                Authentication authentication = new UsernamePasswordAuthenticationToken(principle, null,
                        userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid Token received");
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
}