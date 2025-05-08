package stud.ntnu.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration class for WebSocket messaging in the application.
 * This class configures the WebSocket message broker and STOMP endpoints
 * for real-time communication between the server and clients.
 *
 * <p>The configuration includes:
 * <ul>
 *   <li>Message broker setup for pub/sub messaging</li>
 *   <li>STOMP endpoint registration for client connections</li>
 *   <li>CORS configuration for allowed origins</li>
 *   <li>SockJS fallback support for older browsers</li>
 * </ul>
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  /**
   * Configures the message broker for WebSocket communication.
   * Sets up the following:
   * <ul>
   *   <li>Simple broker for pub/sub messaging on "/topic"</li>
   *   <li>Application destination prefix for client-to-server messages on "/app"</li>
   * </ul>
   *
   * @param config the MessageBrokerRegistry to configure
   */
  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic"); // where messages go out
    config.setApplicationDestinationPrefixes("/app"); // where messages come in
  }

  /**
   * Registers STOMP endpoints for WebSocket connections.
   * Configures:
   * <ul>
   *   <li>WebSocket endpoint at "/ws"</li>
   *   <li>Allowed origins for CORS (localhost:5173 and localhost:8080)</li>
   *   <li>SockJS fallback support for browsers without WebSocket support</li>
   * </ul>
   *
   * @param registry the StompEndpointRegistry to configure
   */
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws") // endpoint clients will connect to
        .setAllowedOriginPatterns("http://localhost:5173",
            "http://localhost:8080") // allow specific origins
        .withSockJS(); // fallback for older browsers
  }
}
