package org.harmoniapp.harmoniwebapi.configuration.AuthorizationManagers;

import org.harmoniapp.harmoniwebapi.configuration.Principle;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Component
public class AdminOrOwnerQueryParamAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext ctx) {
        String queryStr = ctx.getRequest().getQueryString();
        List<String> paramsList = Arrays.stream(queryStr.split("&", -1)).toList();
        Map<String, String> paramsMap = new HashMap<>();
        for (String param : paramsList) {
            String[] split = param.split("=");
            paramsMap.put(split[0], split[1]);
        }

        Long userId = Long.parseLong(paramsMap.get("user_id"));
        if (userId == null) {
            throw new AccessDeniedException("Access denied");
        }
        Authentication authentication = authenticationSupplier.get();
        return new AuthorizationDecision(hasUserId(authentication, userId));
    }

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
