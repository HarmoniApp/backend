package org.harmoniapp.configuration.authmanagers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.configuration.Principle;
import org.harmoniapp.repositories.chat.GroupRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

/**
 * Abstract class that validates group access for authenticated users.
 * Implements the AuthorizationManager interface for RequestAuthorizationContext.
 */
@Component
@RequiredArgsConstructor
public abstract class GroupAccessValidator implements AuthorizationManager<RequestAuthorizationContext> {
    protected final GroupRepository repository;

    /**
     * Determines if the authenticated user is a member of the specified group.
     *
     * @param authentication the authentication object.
     * @param groupId        the ID of the group.
     * @return true if the user is a member of the group, false otherwise.
     * @throws AccessDeniedException if access is denied.
     */
    protected boolean isGroupMember(Authentication authentication, Long groupId) throws AccessDeniedException {
        GrantedAuthority grantedAuthority = authentication.getAuthorities().iterator().next();
        if (grantedAuthority.getAuthority().equals("ROLE_ADMIN") || grantedAuthority.getAuthority().equals("ROLE_USER")) {
            Principle principle = (Principle) authentication.getPrincipal();
            return repository.findById(groupId)
                    .map(group -> group.getMembers().stream().anyMatch(user -> user.getId().equals(principle.id())))
                    .orElse(false);
        }
        return false;
    }
}
