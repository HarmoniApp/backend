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
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

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

    public Claims decodeJWT(String token) {
        SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        return Jwts.parser().verifyWith(secretKey)
                .build().parseSignedClaims(token).getPayload();
    }

    public String getUsername(String token) {
        return String.valueOf(decodeJWT(token).get("username"));
    }

    public Long getUserId(String token) {
        return Long.parseLong(String.valueOf(decodeJWT(token).get("id")));
    }

    public Date getExpiration(Claims claims) {
        return claims.getExpiration();
    }

    public String getAuthorities(String token) {
        return String.valueOf(decodeJWT(token).get("authorities"));
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            return decodeJWT(token).getSubject().equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            return decodeJWT(token).getExpiration().before(new Date(System.currentTimeMillis()));
        } catch (Exception e) {
            return true;
        }
    }
}
