package com.example.Restaurant.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * The WebSocketConfig class is responsible for configuring WebSocket messaging for the application.
 * It implements the WebSocketMessageBrokerConfigurer interface to customize the WebSocket message broker configuration.
 *
 * Main Responsibilities:
 * - Configure the message broker for handling WebSocket messages.
 * - Register STOMP endpoints for WebSocket connections.
 *
 * Component Relationships:
 * - WebSocketMessageBrokerConfigurer: Interface that provides callback methods to configure the WebSocket message broker.
 *
 * Required Dependencies:
 * - Spring WebSocket
 *
 * Security Notes:
 * - Ensure that the WebSocket endpoints are secured appropriately to prevent unauthorized access.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configures the message broker for handling WebSocket messages.
     * Enables a simple in-memory message broker with the "/topic" destination prefix.
     * Sets the application destination prefix to "/app".
     *
     * @param registry the MessageBrokerRegistry used to configure the message broker
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enable a simple in-memory message broker with the "/topic" destination prefix
        registry.enableSimpleBroker("/topic");
        // Set the application destination prefix to "/app"
        registry.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registers STOMP endpoints for WebSocket connections.
     * Adds an endpoint at "/ws-orders" with SockJS fallback options.
     *
     * @param registry the StompEndpointRegistry used to register STOMP endpoints
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Add an endpoint at "/ws-orders" with SockJS fallback options
        registry.addEndpoint("/ws-orders").withSockJS();
    }
}