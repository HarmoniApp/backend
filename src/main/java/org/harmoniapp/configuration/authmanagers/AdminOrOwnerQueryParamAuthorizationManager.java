package org.harmoniapp.configuration.authmanagers;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Authorization manager for checking if the authenticated user is an admin or the owner of a resource
 * based on query param.
 */
@Component
public class AdminOrOwnerQueryParamAuthorizationManager extends AdminOrOwnerValidator implements ExtractQueryParams {

    /**
     * Checks whether the authenticated user is an admin or the owner of the resource based on the `user_id` query parameter.
     *
     * @param authenticationSupplier supplier for the current authentication object
     * @param ctx                    the context containing the request and query parameters
     * @return an {@link AuthorizationDecision} indicating whether access is granted
     */
    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext ctx) {
        Map<String, String> paramsMap = getQueryParams(ctx);

        Long userId = Long.parseLong(paramsMap.get("user_id"));
        if (userId == null) {
            throw new AccessDeniedException("Access denied");
        }
        return new AuthorizationDecision(hasUserId(authenticationSupplier.get(), userId));
    }
}
