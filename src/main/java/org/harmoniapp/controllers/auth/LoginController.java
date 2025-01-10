package org.harmoniapp.controllers.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.contracts.auth.LoginRequestDto;
import org.harmoniapp.contracts.auth.LoginResponseDto;
import org.harmoniapp.services.auth.LoginService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling user login requests.
 * It uses {@link LoginService} to handle the business logic.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {
    private final LoginService service;

    /**
     * Authenticates the user and returns a JWT token in the response.
     *
     * @param loginRequestDTO the login request containing the user's username and password.
     * @return a {@link ResponseEntity} containing the {@link LoginResponseDto} with the JWT token and HTTP status.
     */
    @PostMapping
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDTO) {
        LoginResponseDto response = service.login(loginRequestDTO);
        return ResponseEntity.status(HttpStatus.OK)
                .header("Authorization", "Bearer " + response.jwtToken())
                .body(response);
    }
}
