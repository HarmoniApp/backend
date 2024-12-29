package org.harmoniapp.configuration.authmanagers;

import org.harmoniapp.configuration.Principle;
import org.harmoniapp.repositories.chat.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Authorization manager for checking if the authenticated user is a member of a conversation
 * based on query parameters.
 */
@Component
public class ConversationMemberQueryParamAuthorizationManager extends GroupAccessValidator implements ExtractQueryParams {

    @Autowired
    public ConversationMemberQueryParamAuthorizationManager(GroupRepository repository) {
        super(repository);
    }

    /**
     * Checks whether the authenticated user is a member of a conversation based on query parameters.
     *
     * @param authenticationSupplier supplier for the current authentication object
     * @param ctx                    the context containing the request and query parameters
     * @return an {@link AuthorizationDecision} indicating whether access is granted
     */
    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext ctx) {
        Map<String, String> paramsMap = getQueryParams(ctx);
        Authentication authentication = authenticationSupplier.get();

        if (paramsMap.get("groupId") != null) {
            Long groupId = Long.parseLong(paramsMap.get("groupId"));
            return new AuthorizationDecision(isGroupMember(authentication, groupId));
        } else if (paramsMap.get("userId1") != null && paramsMap.get("userId2") != null) {
            Long userId1 = Long.parseLong(paramsMap.get("userId1"));
            Long userId2 = Long.parseLong(paramsMap.get("userId2"));
            return new AuthorizationDecision(isOnByOneMember(authentication, userId1, userId2));
        } else {
            throw new AccessDeniedException("Conversation participation cannot be determined");
        }
    }

    /**
     * Verifies if the authenticated user is a member of a one-on-one conversation.
     *
     * @param authentication the current authentication object
     * @param userId1        the ID of the first user in the conversation
     * @param userId2        the ID of the second user in the conversation
     * @return {@code true} if the user is a member of the conversation, {@code false} otherwise
     * @throws AccessDeniedException if access is denied
     */
    private boolean isOnByOneMember(Authentication authentication, Long userId1, Long userId2) throws AccessDeniedException {
        GrantedAuthority grantedAuthority = authentication.getAuthorities().iterator().next();
        if (grantedAuthority.getAuthority().equals("ROLE_ADMIN") || grantedAuthority.getAuthority().equals("ROLE_USER")) {
            Principle principle = (Principle) authentication.getPrincipal();
            return principle.id().equals(userId1) || principle.id().equals(userId2);
        }
        return false;
    }
}
