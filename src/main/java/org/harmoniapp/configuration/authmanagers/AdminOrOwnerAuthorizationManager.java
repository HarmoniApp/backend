package org.harmoniapp.configuration.authmanagers;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * Authorization manager for checking if the authenticated user is an admin or the owner of a resource
 * base on path param.
 */
@Component
public class AdminOrOwnerAuthorizationManager extends AdminOrOwnerValidator {

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
}
