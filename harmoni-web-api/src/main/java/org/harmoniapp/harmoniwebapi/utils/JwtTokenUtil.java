package org.harmoniapp.harmoniwebapi.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class for handling JWT token generation, validation, and decoding.
 * <p>
 * This component provides methods to create JWT tokens using user authentication data, decode tokens to extract claims,
 * and validate tokens based on expiration and user details.
 * </p>
 */
@Component
@Getter
public class JwtTokenUtil {

    private final String AUTH_HEADER = "Authorization";
    private final String JWT_ISSUER = "HarmoniApp";

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${jwt.expiration}")
    private Long EXPIRATION;


    public String generateToken(Authentication authentication) {
        return generateToken(authentication, Map.of());
    }

    /**
     * Generates a JWT token for the authenticated user with extra claims.
     *
     * @param authentication the {@link Authentication} object containing user details.
     * @param extraClaims additional claims to add to the token.
     * @return a signed JWT token as a {@link String}.
     */
    public String generateToken(Authentication authentication, Map<String, Object> extraClaims) {
        SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder().issuer(JWT_ISSUER).subject(authentication.getName())
                .claim("username", authentication.getName())
                .claims(extraClaims)
                .claim("authorities", authentication.getAuthorities().stream().map(
                        GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + EXPIRATION))
                .signWith(secretKey).compact();
    }

    /**
     * Decodes a JWT token and extracts the claims.
     *
     * @param token the JWT token to decode.
     * @return a {@link Claims} object containing the token's claims.
     */
    public Claims decodeJWT(String token) {
        SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        return Jwts.parser().verifyWith(secretKey)
                .build().parseSignedClaims(token).getPayload();
    }

    /**
     * Extracts the username from a JWT token.
     *
     * @param token the JWT token to decode.
     * @return the username as a {@link String}.
     */
    public String getUsername(String token) {
        return String.valueOf(decodeJWT(token).get("username"));
    }

    /**
     * Extracts the user ID from a JWT token.
     *
     * @param token the JWT token to decode.
     * @return the user ID as a {@link Long}.
     */
    public Long getUserId(String token) {
        return Long.parseLong(String.valueOf(decodeJWT(token).get("id")));
    }

    /**
     * Extracts the expiration date from a JWT token.
     *
     * @param token the JWT token to decode.
     * @return the expiration date as a {@link Date}.
     */
    public Date getExpiration(String token) {
        return decodeJWT(token).getExpiration();
    }

    /**
     * Extracts the authorities from a JWT token.
     *
     * @param token the JWT token to decode.
     * @return the authorities as a {@link String}.
     */
    public String getAuthorities(String token) {
        return String.valueOf(decodeJWT(token).get("authorities"));
    }

    /**
     * Validates a JWT token based on the user details and expiration data.
     *
     * @param token the JWT token to validate.
     * @param userDetails the {@link UserDetails} object containing user details.
     * @return {@code true} if the token is valid, {@code false} otherwise.
     */
    public boolean isTokenValid(String token, UserDetails userDetails, LocalDate lastPassChange) {
        try {
            return decodeJWT(token).getSubject().equals(userDetails.getUsername())
                    && !isTokenExpired(token)
                    && !isTokenCreatedBeforeLastPassChange(token, lastPassChange);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if a JWT token has expired.
     *
     * @param token the JWT token to check.
     * @return {@code true} if the token has expired, {@code false} otherwise.
     */
    public boolean isTokenExpired(String token) {
        try {
            return decodeJWT(token).getExpiration().before(new Date(System.currentTimeMillis()));
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isTokenCreatedBeforeLastPassChange(String token, LocalDate lastPassChange) {
        //TODO: Change LocalDate to LocalDateTime
        try {
            return decodeJWT(token).getIssuedAt().before(Date.from(lastPassChange.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        } catch (Exception e) {
            return false;
        }
    }
}
