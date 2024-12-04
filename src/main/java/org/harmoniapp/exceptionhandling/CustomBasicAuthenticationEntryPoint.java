package org.harmoniapp.exceptionhandling;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Custom entry point for handling authentication failures in Spring Security.
 * <p>
 * This class implements {@link AuthenticationEntryPoint} to provide a custom response
 * when a user fails to authenticate. It returns a <code>401 Unauthorized</code> status
 * with a detailed JSON response.
 * </p>
 */
public class CustomBasicAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Handles authentication failures by returning a custom JSON response.
     * <p>
     * This method is invoked when a user attempts to authenticate but fails.
     * It sends a 401 Unauthorized status along with a JSON response containing
     * the timestamp, status, error, message, and request path.
     * </p>
     *
     * @param request the {@link HttpServletRequest} that resulted in the exception.
     * @param response the {@link HttpServletResponse} used to send the response.
     * @param authException the exception that caused the authentication to fail.
     * @throws IOException if an input or output error occurs while writing the response.
     * @throws ServletException if a servlet-specific error occurs.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        // Populate dynamic values
        LocalDateTime currentTimeStamp = LocalDateTime.now();
        String message = (authException != null && authException.getMessage() != null) ? authException.getMessage()
                : "Unauthorized";
        String path = request.getRequestURI();
        response.setHeader("harmoni-error-reason", "Authentication failed");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        // Construct the JSON response
        String jsonResponse =
                String.format("{\"timestamp\": \"%s\", \"status\": %d, \"error\": \"%s\", \"message\": \"%s\", \"path\": \"%s\"}",
                        currentTimeStamp, HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                        message, path);
        response.getWriter().write(jsonResponse);
    }
}
