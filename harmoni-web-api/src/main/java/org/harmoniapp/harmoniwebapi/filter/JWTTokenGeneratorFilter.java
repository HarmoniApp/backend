package org.harmoniapp.harmoniwebapi.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.repositories.UserRepository;
import org.harmoniapp.harmoniwebapi.utils.JwtTokenUtil;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * A filter that generates a JWT token after successful authentication.
 * <p>
 * This class extends {@link OncePerRequestFilter}, ensuring the JWT token is generated only once per request,
 * specifically for the `/login` endpoint. After authentication, it generates a JWT token using the user’s
 * details and adds it to the response headers.
 * </p>
 */
@RequiredArgsConstructor
public class JWTTokenGeneratorFilter extends OncePerRequestFilter {
    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;

    /**
     * Generates a JWT token after successful authentication and adds it to the response header.
     * <p>
     * This method retrieves the current {@link Authentication} from the {@link SecurityContextHolder}, generates
     * a JWT token using the user's name and authorities, and sets it as a header in the response. The token is
     * signed with the application's secret key and is valid for ~8 hours.
     * </p>
     *
     * @param request     the {@link HttpServletRequest} object that contains the client's request.
     * @param response    the {@link HttpServletResponse} object that contains the filter's response.
     * @param filterChain the {@link FilterChain} used to invoke the next filter in the chain.
     * @throws ServletException if the request cannot be handled.
     * @throws IOException      if an input or output error occurs during filtering.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null != authentication) {
            User user = userRepository.findByEmail(authentication.getName()).orElse(null);
            if (null == user) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
                return;
            }
            if (!user.isActive()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User is not active");
                return;
            }
            String secret = jwtTokenUtil.getSECRET_KEY();
            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            String jwt = Jwts.builder().issuer("HarmoniApp").subject("JWT Token")
                    .claim("username", authentication.getName())
                    .claim("employeeId", "")
                    .claim("id", "")
                    .claim("authorities", authentication.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                    .issuedAt(new Date())
                    .expiration(new Date((new Date()).getTime() + 30000000)) // ~8 hours
                    .signWith(secretKey)
                    .compact();
            response.setHeader(jwtTokenUtil.getAUTH_HEADER(), jwt);
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Specifies that this filter should only apply to the `/login` endpoint.
     *
     * @param request the {@link HttpServletRequest} object.
     * @return {@code true} if the filter should be skipped for this request; {@code false} otherwise.
     * @throws ServletException if an error occurs during filtering.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !request.getServletPath().equals("/login"); //login path
    }
}
