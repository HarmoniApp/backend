package org.harmoniapp.harmoniwebapi.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.entities.User;
import org.harmoniapp.harmonidata.repositories.RepositoryCollector;
import org.harmoniapp.harmoniwebapi.constant.JWTConstant;
import org.harmoniapp.harmoniwebapi.contracts.LoginRequestDTO;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final RepositoryCollector repositoryCollector;
    private final AuthenticationManager authenticationManager;
    private final Environment env;


    public String login(LoginRequestDTO loginRequest) {
        String jwt = "";
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(),
                loginRequest.password());
        Authentication authenticationResponse = authenticationManager.authenticate(authentication);
        if(null != authenticationResponse && authenticationResponse.isAuthenticated()) {
            User user = repositoryCollector.getUsers().findByEmail(loginRequest.username()).orElse(null);
            if (user == null) {
                throw new RuntimeException("User not found!");
            }
            if (null != env) {
                String secret = env.getProperty(JWTConstant.JWT_SECRET_KEY,
                        JWTConstant.JWT_SECRET_DEFAULT_VALUE);
                SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                jwt = Jwts.builder().issuer("HarmoniApp").subject("JWT Token")
                        .claim("username", authenticationResponse.getName())
                        .claim("employeeId", user.getEmployeeId())
                        .claim("id", user.getId())
                        .claim("authorities", authenticationResponse.getAuthorities().stream().map(
                                GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                        .issuedAt(new Date())
                        .expiration(new Date((new Date()).getTime() + 30000000))
                        .signWith(secretKey).compact();
            }
        }
        return jwt;
    }
}
