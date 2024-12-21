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
     * Handles an authentication failure and sends a custom JSON response.
     *
     * @param request       the {@link HttpServletRequest} that resulted in an {@link AuthenticationException}.
     * @param response      the {@link HttpServletResponse} to send the error response.
     * @param authException the exception that caused the authentication to fail.
     * @throws IOException      if an input or output error occurs while handling the error response.
     * @throws ServletException if the request could not be handled.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        String message = getMessage(authException);
        String path = request.getRequestURI();
        String jsonResponse = constructJsonResponse(message, path);

        setResponseHeaders(response);
        response.getWriter().write(jsonResponse);
    }

    /**
     * Retrieves the message from the given AuthenticationException.
     *
     * @param authException the exception that caused the authentication to fail.
     * @return the message from the exception, or "Unauthorized" if the exception or its message is null.
     */
    private String getMessage(AuthenticationException authException) {
        return (authException != null && authException.getMessage() != null) ? authException.getMessage() : "Unauthorized";
    }

    /**
     * Sets the response headers for an authentication failure response.
     * <p>
     * This method sets the HTTP status to 401 Unauthorized, the content type to JSON,
     * and adds a custom header indicating the reason for the failure.
     * </p>
     *
     * @param response the {@link HttpServletResponse} used to send the response.
     */
    private void setResponseHeaders(HttpServletResponse response) {
        response.setHeader("harmoni-error-reason", "Authentication failed");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
    }

    /**
     * Constructs a JSON response string for an authentication failure.
     *
     * @param message the message describing the authentication failure.
     * @param path    the request path where the authentication failure occurred.
     * @return a JSON string containing the timestamp, status, error, message, and request path.
     */
    private String constructJsonResponse(String message, String path) {
        return String.format("{\"timestamp\": \"%s\", \"status\": %d, \"error\": \"%s\", \"message\": \"%s\", \"path\": \"%s\"}",
                LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                message, path);
    }
}
