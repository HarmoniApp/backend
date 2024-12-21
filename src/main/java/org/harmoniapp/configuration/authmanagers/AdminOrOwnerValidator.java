package org.harmoniapp.configuration.authmanagers;

import org.harmoniapp.configuration.Principle;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

/**
 * Abstract class that validates if the authenticated user is either an admin or the owner of the resource.
 * Implements the AuthorizationManager interface for RequestAuthorizationContext.
 */
public abstract class AdminOrOwnerValidator implements AuthorizationManager<RequestAuthorizationContext> {

    /**
     * Verifies if the authenticated user is either an admin or the owner of the resource.
     *
     * @param authentication the current authentication object
     * @param id the ID of the user to verify ownership against
     * @return {@code true} if the user is an admin or the owner, {@code false} otherwise
     * @throws AccessDeniedException if access is denied
     */
    protected boolean hasUserId(Authentication authentication, Long id) throws AccessDeniedException {
        GrantedAuthority grantedAuthority = authentication.getAuthorities().iterator().next();
        if (grantedAuthority.getAuthority().equals("ROLE_ADMIN")) {
            return true;
        } else if (grantedAuthority.getAuthority().equals("ROLE_USER")) {
            Principle principle = (Principle) authentication.getPrincipal();
            return principle.id().equals(id);
        } else {
            return false;
        }
    }
}
