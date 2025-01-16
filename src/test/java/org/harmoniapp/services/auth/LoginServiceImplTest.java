package org.harmoniapp.services.auth;

import org.harmoniapp.contracts.auth.LoginRequestDto;
import org.harmoniapp.contracts.auth.LoginResponseDto;
import org.harmoniapp.entities.user.User;
import org.harmoniapp.repositories.RepositoryCollector;
import org.harmoniapp.repositories.user.UserRepository;
import org.harmoniapp.utils.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginServiceImplTest {

    @Mock
    private RepositoryCollector repositoryCollector;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private LoginServiceImpl loginService;

    @Mock
    private User user;

    @Mock
    private Authentication authentication;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Test
    public void loginTest() {
        LoginRequestDto loginRequest = new LoginRequestDto("user@example.com", "password");
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findByEmail(loginRequest.username())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(user.getPasswordExpirationDate()).thenReturn(LocalDate.now().plusDays(1));
        when(jwtTokenUtil.generateToken(any(Authentication.class), anyMap(), anyBoolean())).thenReturn("jwt-token");

        LoginResponseDto response = loginService.login(loginRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.getReasonPhrase(), response.status());
        assertEquals("jwt-token", response.jwtToken());
        assertNull(response.path());
    }

    @Test
    public void loginUserNotFoundTest() {
        LoginRequestDto loginRequest = new LoginRequestDto("user@example.com", "password");
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findByEmail(loginRequest.username())).thenReturn(Optional.empty());

        assertThrows(AccessDeniedException.class, () -> loginService.login(loginRequest));
    }

    @Test
    public void loginPasswordExpiredTest() {
        LoginRequestDto loginRequest = new LoginRequestDto("user@example.com", "password");
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findByEmail(loginRequest.username())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(user.getPasswordExpirationDate()).thenReturn(LocalDate.now().minusDays(1));
        when(jwtTokenUtil.generateToken(any(Authentication.class), anyMap(), anyBoolean())).thenReturn("jwt-token");
        when(user.getId()).thenReturn(1L);

        LoginResponseDto response = loginService.login(loginRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.getReasonPhrase(), response.status());
        assertEquals("jwt-token", response.jwtToken());
        assertEquals(contextPath + "/user/1/changePassword", response.path());
    }

    @Test
    public void loginInvalidCredentialsTest() {
        LoginRequestDto loginRequest = new LoginRequestDto("user@example.com", "password");
        when(repositoryCollector.getUsers()).thenReturn(userRepository);
        when(userRepository.findByEmail(loginRequest.username())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(Authentication.class))).thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(AccessDeniedException.class, () -> loginService.login(loginRequest));
    }
}