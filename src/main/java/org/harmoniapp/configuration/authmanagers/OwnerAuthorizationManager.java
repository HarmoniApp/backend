package org.harmoniapp.configuration.authmanagers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.configuration.Principle;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * Authorization manager for checking if the authenticated user is the owner of a resource.
 */
@Component
@RequiredArgsConstructor
public class OwnerAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    /**
     * Checks whether the authenticated user is the owner of the resource based on the user ID in the request context.
     *
     * @param authenticationSupplier supplier for the current authentication object
     * @param ctx                    the context containing request variables like the user ID
     * @return an {@link AuthorizationDecision} indicating whether access is granted or denied
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
     * Determines whether the authenticated user matches the provided user ID.
     *
     * @param authentication the current authentication object
     * @param id             the ID of the user to check ownership against
     * @return {@code true} if the user is authenticated and their ID matches, {@code false} otherwise
     */
    private boolean hasUserId(Authentication authentication, Long id) throws AccessDeniedException {
        GrantedAuthority authority = authentication.getAuthorities().iterator().next();
        if (authority.getAuthority().equals("ROLE_ANONYMOUS")) {
            return false;
        }

        Principle principle = (Principle) authentication.getPrincipal();
        return principle.id().equals(id);
    }
}
