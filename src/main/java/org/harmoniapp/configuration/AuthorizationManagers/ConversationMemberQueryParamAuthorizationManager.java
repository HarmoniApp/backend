package org.harmoniapp.configuration.AuthorizationManagers;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.repositories.GroupRepository;
import org.harmoniapp.configuration.Principle;
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

/**
 * Authorization manager for checking if the authenticated user is a member of a conversation
 * based on query parameters.
 */
@Component
@RequiredArgsConstructor
public class ConversationMemberQueryParamAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    private final GroupRepository repository;

    /**
     * Checks whether the authenticated user is a member of a conversation based on query parameters.
     *
     * @param authenticationSupplier supplier for the current authentication object
     * @param ctx the context containing the request and query parameters
     * @return an {@link AuthorizationDecision} indicating whether access is granted
     */
    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext ctx) {
        String queryStr = ctx.getRequest().getQueryString();
        List<String> paramsList = Arrays.stream(queryStr.split("&", -1)).toList();
        Map<String, String> paramsMap = new HashMap<>();
        for (String param : paramsList) {
            String[] split = param.split("=");
            paramsMap.put(split[0], split[1]);
        }

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
     * @param userId1 the ID of the first user in the conversation
     * @param userId2 the ID of the second user in the conversation
     * @return {@code true} if the user is a member of the conversation, {@code false} otherwise
     * @throws AccessDeniedException if access is denied
     */
    private boolean isOnByOneMember(Authentication authentication, Long userId1, Long userId2) throws AccessDeniedException {
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) authentication.getAuthorities();

        GrantedAuthority grantedAuthority = authorities.getFirst();
        if (grantedAuthority.getAuthority().equals("ROLE_ADMIN") || grantedAuthority.getAuthority().equals("ROLE_USER")) {
            Principle principle = (Principle) authentication.getPrincipal();
            return principle.id().equals(userId1) || principle.id().equals(userId2);
        } else {
            return false;
        }
    }

    /**
     * Verifies if the authenticated user is a member of a group conversation.
     *
     * @param authentication the current authentication object
     * @param groupId the ID of the group
     * @return {@code true} if the user is a member of the group, {@code false} otherwise
     * @throws AccessDeniedException if access is denied
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
