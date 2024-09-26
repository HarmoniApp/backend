package org.harmoniapp.harmoniwebapi.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.utils.JwtTokenUtil;
import org.springframework.core.env.Environment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JWTTokenGeneratorFilter extends OncePerRequestFilter {
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null != authentication) {
            Environment env = getEnvironment();
            if (null != env) {
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
                response.setHeader(JWTConstant.JWT_HEADER, jwt);
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !request.getServletPath().equals("/login"); //login path
    }
}
