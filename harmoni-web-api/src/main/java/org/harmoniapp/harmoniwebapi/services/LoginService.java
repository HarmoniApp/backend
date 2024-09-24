package org.harmoniapp.harmoniwebapi.services;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.contracts.LoginRequestDTO;
import org.harmoniapp.harmoniwebapi.utils.JwtTokenUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final RepositoryCollector repositoryCollector;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;


    public String login(LoginRequestDTO loginRequest) {
        String jwt = "";
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(),
                loginRequest.password());
        Authentication authenticationResponse = authenticationManager.authenticate(authentication);

        assert authenticationResponse != null;
        if (authenticationResponse.isAuthenticated()) {
            User user = repositoryCollector.getUsers().findByEmail(loginRequest.username()).orElse(null);
            if (user == null) {
                throw new RuntimeException("User not found!");
            }

            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("id", user.getId());

            jwt = jwtTokenUtil.generateToken(authenticationResponse, extraClaims);
        }
        return jwt;
    }
}
