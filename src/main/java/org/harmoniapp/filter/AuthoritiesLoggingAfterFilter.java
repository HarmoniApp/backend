package org.harmoniapp.filter;

import jakarta.servlet.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

/**
 * A filter that logs the authorities of successfully authenticated users.
 * <p>
 * This class implements {@link Filter} and logs the authentication details after a successful authentication event,
 * specifically the user's name and their granted authorities. It uses SLF4J for logging purposes.
 * </p>
 */
@Slf4j
public class AuthoritiesLoggingAfterFilter implements Filter {

    /**
     * Logs the authorities of an authenticated user.
     * <p>
     * This method is invoked during the filter chain and checks if the current {@link Authentication} object is available.
     * If the user is authenticated, it logs the username and the user's authorities. The request is then passed along the filter chain.
     * </p>
     *
     * @param request the {@link ServletRequest} object that contains the client's request.
     * @param response the {@link ServletResponse} object that contains the filter's response.
     * @param chain the {@link FilterChain} used to invoke the next filter in the chain.
     * @throws IOException if an input or output error occurs during filtering.
     * @throws ServletException if the request cannot be handled.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(null != authentication) {
            log.info("User " + authentication.getName() + " is successfully authenticated and "
                    + "has the authorities " + authentication.getAuthorities().toString());
        }
        chain.doFilter(request,response);
    }
}
