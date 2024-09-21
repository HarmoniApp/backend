package org.harmoniapp.harmoniwebapi.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.harmoniapp.harmoniwebapi.constant.JWTConstant;
import org.harmoniapp.harmoniwebapi.configuration.Principle;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JWTTokenValidationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = request.getHeader(JWTConstant.JWT_HEADER);

        if (null != jwt) {
            if (!StringUtils.startsWithIgnoreCase(jwt, "Bearer ")) {
                throw new BadCredentialsException("Invalid Token received");
            }
            jwt = jwt.substring(7);
            try {
                Environment env = getEnvironment();
                if (null != env) {
                    String secret = env.getProperty(JWTConstant.JWT_SECRET_KEY, JWTConstant.JWT_SECRET_DEFAULT_VALUE);
                    SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                    if (null != secretKey) {
                        Claims claims = Jwts.parser().verifyWith(secretKey)
                                .build().parseSignedClaims(jwt).getPayload();
                        String username = String.valueOf(claims.get("username"));
                        String authorities = String.valueOf(claims.get("authorities"));
                        String employeeId = String.valueOf(claims.get("employeeId"));
                        Long id = Long.parseLong(String.valueOf(claims.get("id")));
                        Principle principle = new Principle(id, employeeId, username);
                        Authentication authentication = new UsernamePasswordAuthenticationToken(principle, null,
                                AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
                throw new BadCredentialsException("Invalid Token received");
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getServletPath().equals("/login"); //login path
    }
}
