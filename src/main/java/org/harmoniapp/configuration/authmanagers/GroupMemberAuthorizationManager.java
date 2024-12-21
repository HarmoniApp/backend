package org.harmoniapp.configuration.authmanagers;

import org.harmoniapp.repositories.chat.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * Authorization manager to check if the authenticated user is a member of a group.
 */
@Component
public class GroupMemberAuthorizationManager extends GroupAccessValidator {

    @Autowired
    public GroupMemberAuthorizationManager(GroupRepository repository) {
        super(repository);
    }

    /**
     * Checks if the authenticated user is authorized to access the requested resource.
     *
     * @param authenticationSupplier the supplier of the authentication object.
     * @param ctx                    the request authorization context.
     * @return the authorization decision.
     */
    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext ctx) {
        try {
            Long groupId = Long.parseLong(ctx.getVariables().get("groupId"));
            Authentication authentication = authenticationSupplier.get();
            return new AuthorizationDecision(isGroupMember(authentication, groupId));
        } catch (AccessDeniedException e) {
            return new AuthorizationDecision(false);
        }
    }
}
