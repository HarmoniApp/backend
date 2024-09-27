package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.LoginRequestDto;
import org.harmoniapp.harmoniwebapi.contracts.LoginResponseDto;
import org.harmoniapp.harmoniwebapi.services.LoginService;
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

    @PostMapping
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDTO) {
        String jwt = service.login(loginRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).header("Authorization", "Bearer " + jwt)
                .body(new LoginResponseDto(HttpStatus.OK.getReasonPhrase(), jwt));
    }
}
