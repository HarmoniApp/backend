package org.harmoniapp.harmoniwebapi.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmoniwebapi.configuration.HarmoniUserDetailsService;
import org.harmoniapp.harmoniwebapi.configuration.Principle;
import org.harmoniapp.harmoniwebapi.utils.JwtTokenUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTTokenValidationFilter extends OncePerRequestFilter {


    private final JwtTokenUtil jwtTokenUtil;
    private final HarmoniUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = request.getHeader(jwtTokenUtil.getAUTH_HEADER());
        String username = null;

        assert jwt != null;
        if (!StringUtils.startsWithIgnoreCase(jwt, "Bearer ")) {
            throw new BadCredentialsException("Invalid Token received");
        }
        jwt = jwt.substring(7);
        try {
            username = jwtTokenUtil.getUsername(jwt);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtTokenUtil.isTokenValid(jwt, userDetails)) {
                Long id = jwtTokenUtil.getUserId(jwt);
                Principle principle = new Principle(id, username);
                Authentication authentication = new UsernamePasswordAuthenticationToken(principle, null,
                        userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid Token received");
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getServletPath().equals("/login"); //login path
    }
}