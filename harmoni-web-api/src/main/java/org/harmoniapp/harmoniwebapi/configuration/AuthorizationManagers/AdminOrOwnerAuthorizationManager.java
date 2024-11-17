package org.harmoniapp.harmoniwebapi.configuration.AuthorizationManagers;

import org.harmoniapp.harmoniwebapi.configuration.Principle;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;

/**
 * Authorization manager for checking if the authenticated user is an admin or the owner of a resource
 * base on path param.
 */
@Component
public class AdminOrOwnerAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    /**
     * Checks if the authenticated user is an admin or the owner of a resource.
     *
     * @param authenticationSupplier the {@link Supplier} object containing the user's authentication details.
     * @param ctx                    the {@link RequestAuthorizationContext} object containing the request context.
     * @return an {@link AuthorizationDecision} object containing the authorization decision.
     */
    @Override
    public AuthorizationDecision check(Supplier authenticationSupplier, RequestAuthorizationContext ctx) {
        try {
            Long userId = Long.parseLong(ctx.getVariables().get("id"));
            Authentication authentication = (Authentication) authenticationSupplier.get();
            return new AuthorizationDecision(hasUserId(authentication, userId));
        } catch (AccessDeniedException e) {
            return new AuthorizationDecision(false);
        }
    }

    /**
     * Checks if the authenticated user has the necessary permissions.
     *
     * @param authentication the current authentication object
     * @param id the ID of the user to verify ownership against
     * @return {@code true} if the user is an admin or the owner, {@code false} otherwise
     * @throws AccessDeniedException if access is denied
     */
    private boolean hasUserId(Authentication authentication, Long id) throws AccessDeniedException {
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) authentication.getAuthorities();

        GrantedAuthority grantedAuthority = authorities.getFirst();
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