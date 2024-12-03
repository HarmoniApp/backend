package org.harmoniapp.harmoniwebapi.configuration.AuthorizationManagers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.harmonidata.repositories.GroupRepository;
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
 * Authorization manager to check if the authenticated user is a member of a group.
 */
@Component
@RequiredArgsConstructor
public class GroupMemberAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    private final GroupRepository repository;

    /**
     * Checks if the authenticated user is authorized to access the requested resource.
     *
     * @param authenticationSupplier the supplier of the authentication object.
     * @param ctx the request authorization context.
     * @return the authorization decision.
     */
    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext ctx) {
        try {
            Long groupId = Long.parseLong(ctx.getVariables().get("groupId"));
            Authentication authentication = (Authentication) authenticationSupplier.get();
            return new AuthorizationDecision(isGroupMember(authentication, groupId));
        } catch (AccessDeniedException e) {
            return new AuthorizationDecision(false);
        }
    }

    /**
     * Determines if the authenticated user is a member of the specified group.
     *
     * @param authentication the authentication object.
     * @param groupId the ID of the group.
     * @return true if the user is a member of the group, false otherwise.
     * @throws AccessDeniedException if access is denied.
     */
    private boolean isGroupMember(Authentication authentication, Long groupId) throws AccessDeniedException {
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) authentication.getAuthorities();

        GrantedAuthority grantedAuthority = authorities.getFirst();
        if (grantedAuthority.getAuthority().equals("ROLE_ADMIN") || grantedAuthority.getAuthority().equals("ROLE_USER")) {
            Principle principle = (Principle) authentication.getPrincipal();
            return repository.findById(groupId)
                    .map(group -> group.getMembers().stream().anyMatch(user -> user.getId().equals(principle.id())))
                    .orElse(false);
        } else {
            return false;
        }
    }
}
