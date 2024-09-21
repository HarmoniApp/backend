package org.harmoniapp.harmoniwebapi.configuration.AuthorizationManagers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.repositories.UserRepository;
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

@Component
@RequiredArgsConstructor
public class OwnerAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    private final UserRepository repository;

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

    private boolean hasUserId(Authentication authentication, Long id) throws AccessDeniedException {
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) authentication.getAuthorities();
        if (authorities.getFirst().getAuthority().equals("ROLE_ANONYMOUS")) {
            return false;
        }

        Principle principle = (Principle) authentication.getPrincipal();
        return principle.id().equals(id);
    }
}
