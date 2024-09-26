package org.harmoniapp.harmoniwebapi.controllers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.contracts.LoginRequestDTO;
import org.harmoniapp.harmoniwebapi.contracts.LoginResponseDTO;
import org.harmoniapp.harmoniwebapi.services.LoginService;
import org.harmoniapp.harmoniwebapi.utils.JwtTokenUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {
    private final LoginService service;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        String jwt = service.login(loginRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).header(jwtTokenUtil.getAUTH_HEADER(), "Bearer " + jwt)
                .body(new LoginResponseDTO(HttpStatus.OK.getReasonPhrase(), jwt));
    }
}
