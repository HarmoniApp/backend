package org.harmoniapp.configuration;

import lombok.RequiredArgsConstructor;
import org.harmoniapp.utils.JwtTokenUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket configuration class that enables WebSocket message broker and configures
 * STOMP endpoints, message broker, and client inbound channel.
 */
@Configuration
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final JwtTokenUtil jwtTokenUtil;
    private final HarmoniUserDetailsService userDetailsService;

    /**
     * Configures the message broker with application destination prefixes and enables a simple broker.
     *
     * @param config the MessageBrokerRegistry to configure
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/client");
        config.setApplicationDestinationPrefixes("/server");
    }

    /**
     * Registers STOMP endpoints with allowed origins and SockJS support.
     *
     * @param registry the StompEndpointRegistry to configure
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // api/v1/ws
                .setAllowedOrigins("http://localhost:3000")
                .withSockJS();
    }

    /**
     * Configures the client inbound channel with a custom interceptor for handling authentication.
     *
     * @param registration the ChannelRegistration to configure
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String token = extractToken(accessor);
                    if (token != null) {
                        authenticateUser(token, accessor);
                    }
                }
                return message;
            }
        });
    }

    /**
     * Extracts the JWT token from the STOMP header accessor.
     *
     * @param accessor the StompHeaderAccessor containing the headers
     * @return the extracted JWT token, or null if not found
     */
    private String extractToken(StompHeaderAccessor accessor) {
        String authorizationHeader = accessor.getFirstNativeHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    /**
     * Authenticates the user using the JWT token and sets the authentication in the security context.
     *
     * @param token    the JWT token
     * @param accessor the StompHeaderAccessor to set the user
     */
    private void authenticateUser(String token, StompHeaderAccessor accessor) {
        String username = jwtTokenUtil.getUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        accessor.setUser(authentication);
    }
}
