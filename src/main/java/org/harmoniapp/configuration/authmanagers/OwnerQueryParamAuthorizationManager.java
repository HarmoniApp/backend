package org.harmoniapp.configuration.authmanagers;

import org.harmoniapp.configuration.Principle;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Authorization manager that checks if the authenticated user has the same user ID as specified in the query parameters.
 */
@Component
public class OwnerQueryParamAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext>, ExtractQueryParams {

    /**
     * Checks if the authenticated user has the same user ID as specified in the query parameters.
     *
     * @param authenticationSupplier the supplier of the authentication object.
     * @param ctx                    the request authorization context.
     * @return an authorization decision indicating whether access is granted or denied.
     */
    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext ctx) {
        Map<String, String> paramsMap = getQueryParams(ctx);

        Long userId = Long.parseLong(paramsMap.get("userId"));
        if (userId == null) {
            throw new AccessDeniedException("Odmowa dostępu");
        }
        Authentication authentication = authenticationSupplier.get();
        return new AuthorizationDecision(hasUserId(authentication, userId));
    }

    /**
     * Determines if the authenticated user has the specified user ID.
     *
     * @param authentication the authentication object containing user details.
     * @param id             the user ID to check against.
     * @return true if the authenticated user has the specified user ID, false otherwise.
     * @throws AccessDeniedException if access is denied.
     */
    private boolean hasUserId(Authentication authentication, Long id) throws AccessDeniedException {
        GrantedAuthority grantedAuthority = authentication.getAuthorities().iterator().next();
        if (grantedAuthority.getAuthority().equals("ROLE_ADMIN") || grantedAuthority.getAuthority().equals("ROLE_USER")) {
            Principle principle = (Principle) authentication.getPrincipal();
            return principle.id().equals(id);
        } else {
            return false;
        }
    }
}
