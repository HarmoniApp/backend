package org.harmoniapp.harmoniwebapi.exceptionhandling;

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
     * Handles access denied exceptions by returning a custom JSON response.
     * <p>
     * This method is invoked when a user attempts to access a protected resource without sufficient authorization.
     * It sends a 403 Forbidden status along with a JSON response containing the timestamp, status, error, message, and request path.
     * </p>
     *
     * @param request the {@link HttpServletRequest} that resulted in the exception.
     * @param response the {@link HttpServletResponse} used to send the response.
     * @param accessDeniedException the exception that caused the access to be denied.
     * @throws IOException if an input or output error occurs while writing the response.
     * @throws ServletException if a servlet-specific error occurs.
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // Populate dynamic values
        LocalDateTime currentTimeStamp = LocalDateTime.now();
        String message = (accessDeniedException != null && accessDeniedException.getMessage() != null) ?
                accessDeniedException.getMessage() : "Authorization failed";
        String path = request.getRequestURI();
        response.setHeader("haromi-denied-reason", "Authorization failed");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json;charset=UTF-8");
        // Construct the JSON response
        String jsonResponse =
                String.format("{\"timestamp\": \"%s\", \"status\": %d, \"error\": \"%s\", \"message\": \"%s\", \"path\": \"%s\"}",
                        currentTimeStamp, HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                        message, path);
        response.getWriter().write(jsonResponse);
    }
}
