package org.harmoniapp.exceptionhandling;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Custom handler for access denied situations in Spring Security.
 * <p>
 * This class implements {@link AccessDeniedHandler} to provide a custom response
 * when a user tries to access a resource they are not authorized to access.
 * It returns a <code>403 Forbidden</code> status with a detailed JSON response.
 * </p>
 */
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * Handles access denied exception by constructing a JSON response and setting the appropriate headers.
     *
     * @param request               the {@link HttpServletRequest} that resulted in an {@link AccessDeniedException}.
     * @param response              the {@link HttpServletResponse} to send the response.
     * @param accessDeniedException the exception that caused the access to be denied.
     * @throws IOException      if an input or output exception occurs.
     * @throws ServletException if a servlet-specific exception occurs.
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        String message = getMessage(accessDeniedException);
        String path = request.getRequestURI();
        String jsonResponse = constructJsonResponse(message, path);

        setResponseHeaders(response);
        response.getWriter().write(jsonResponse);
    }

    /**
     * Retrieves the message from the AccessDeniedException.
     *
     * @param accessDeniedException the exception that caused the access to be denied.
     * @return the message from the exception, or a default message if the exception or its message is null.
     */
    private String getMessage(AccessDeniedException accessDeniedException) {
        return (accessDeniedException != null && accessDeniedException.getMessage() != null) ?
                accessDeniedException.getMessage() : "Authorization failed";
    }

    /**
     * Sets the response headers for access denied response.
     * <p>
     * This method sets the HTTP status to 403 Forbidden, the content type to JSON,
     * and adds a custom header indicating the reason for the denial.
     * </p>
     *
     * @param response the {@link HttpServletResponse} used to send the response.
     */
    private void setResponseHeaders(HttpServletResponse response) {
        response.setHeader("haromi-denied-reason", "Authorization failed");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json;charset=UTF-8");
    }

    /**
     * Constructs a JSON response string for access denied situations.
     *
     * @param message the message describing the reason for the access denial.
     * @param path    the request path that was attempted to be accessed.
     * @return a JSON string containing the timestamp, status, error, message, and path.
     */
    private String constructJsonResponse(String message, String path) {
        return String.format("{\"timestamp\": \"%s\", \"status\": %d, \"error\": \"%s\", \"message\": \"%s\", \"path\": \"%s\"}",
                LocalDateTime.now(), HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                message, path);
    }
}
