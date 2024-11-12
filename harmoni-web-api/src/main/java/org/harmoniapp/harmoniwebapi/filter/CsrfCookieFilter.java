package org.harmoniapp.harmoniwebapi.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * A filter for ensuring that the CSRF token is included in the request.
 * <p>
 * This class extends {@link OncePerRequestFilter}, ensuring that the filter is executed only once per request.
 * It retrieves the {@link CsrfToken} from the request attributes and ensures that it is processed. This filter is
 * typically used to ensure that CSRF tokens are handled properly in the security configuration.
 * </p>
 */
public class CsrfCookieFilter extends OncePerRequestFilter {

    /**
     * Processes the CSRF token for the request.
     * <p>
     * This method retrieves the {@link CsrfToken} from the request attributes and ensures it is accessed, which
     * can help initialize or refresh the CSRF token in the response. After processing the CSRF token, the request
     * is passed along the filter chain.
     * </p>
     *
     * @param request the {@link HttpServletRequest} object that contains the client's request.
     * @param response the {@link HttpServletResponse} object that contains the filter's response.
     * @param filterChain the {@link FilterChain} used to invoke the next filter in the chain.
     * @throws ServletException if the request cannot be handled.
     * @throws IOException if an input or output error occurs during filtering.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        csrfToken.getToken();

        filterChain.doFilter(request, response);
    }
}
