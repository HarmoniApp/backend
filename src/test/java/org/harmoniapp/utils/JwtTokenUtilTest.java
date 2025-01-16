package org.harmoniapp.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JwtTokenUtilTest {

    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtTokenUtil = new JwtTokenUtil("mysecretkeymysecretkeymysecretkeymysecretkey", 3600000L, 600000L);
    }

    @Test
    public void generateTokenTest() {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = mock(UsernamePasswordAuthenticationToken.class);
        GrantedAuthority authority = mock(GrantedAuthority.class);
        when(usernamePasswordAuthenticationToken.getAuthorities()).thenReturn(List.of(authority));
        when(authority.getAuthority()).thenReturn("ROLE_USER");
        when(authentication.getName()).thenReturn("user");

        String token = jwtTokenUtil.generateToken(authentication, Map.of("key", "value"), false);

        assertNotNull(token);
    }

    @Test
    public void decodeJWTTest() {
        SecretKey secretKey = Keys.hmacShaKeyFor("mysecretkeymysecretkeymysecretkeymysecretkey".getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder().subject("user").signWith(secretKey).compact();

        Claims claims = jwtTokenUtil.decodeJWT(token);

        assertNotNull(claims);
        assertEquals("user", claims.getSubject());
    }

    @Test
    public void getUsernameTest() {
        SecretKey secretKey = Keys.hmacShaKeyFor("mysecretkeymysecretkeymysecretkeymysecretkey".getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder().subject("user").claim("username", "user").signWith(secretKey).compact();

        String username = jwtTokenUtil.getUsername(token);

        assertEquals("user", username);
    }

    @Test
    public void getUserIdTest() {
        SecretKey secretKey = Keys.hmacShaKeyFor("mysecretkeymysecretkeymysecretkeymysecretkey".getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder().subject("user").claim("id", 1L).signWith(secretKey).compact();

        Long userId = jwtTokenUtil.getUserId(token);

        assertEquals(1L, userId);
    }

    @Test
    public void getExpirationTest() {
        Date now = new Date();
        SecretKey secretKey = Keys.hmacShaKeyFor("mysecretkeymysecretkeymysecretkeymysecretkey".getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder().subject("user").expiration(new Date(now.getTime() + 3600000L)).signWith(secretKey).compact();

        Date expiration = jwtTokenUtil.getExpiration(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(now));
    }

    @Test
    public void getAuthoritiesTest() {
        SecretKey secretKey = Keys.hmacShaKeyFor("mysecretkeymysecretkeymysecretkeymysecretkey".getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder().subject("user").claim("authorities", "ROLE_USER").signWith(secretKey).compact();

        String authorities = jwtTokenUtil.getAuthorities(token);

        assertEquals("ROLE_USER", authorities);
    }

    @Test
    public void isTokenValidTest() {
        when(userDetails.getUsername()).thenReturn("user");
        SecretKey secretKey = Keys.hmacShaKeyFor("mysecretkeymysecretkeymysecretkeymysecretkey".getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder().subject("user").expiration(new Date(System.currentTimeMillis() + 3600000L)).signWith(secretKey).compact();

        boolean isValid = jwtTokenUtil.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    public void isTokenExpiredTest() {
        SecretKey secretKey = Keys.hmacShaKeyFor("mysecretkeymysecretkeymysecretkeymysecretkey".getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder().subject("user").expiration(new Date(System.currentTimeMillis() - 3600000L)).signWith(secretKey).compact();

        boolean isExpired = jwtTokenUtil.isTokenExpired(token);

        assertTrue(isExpired);
    }
}