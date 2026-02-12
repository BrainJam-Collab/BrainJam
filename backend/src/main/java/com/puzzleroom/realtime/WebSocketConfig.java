package com.puzzleroom.realtime;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final RoomSocketHandler roomSocketHandler;
    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    public WebSocketConfig(RoomSocketHandler roomSocketHandler, JwtHandshakeInterceptor jwtHandshakeInterceptor) {
        this.roomSocketHandler = roomSocketHandler;
        this.jwtHandshakeInterceptor = jwtHandshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
            .addHandler(roomSocketHandler, "/ws/rooms")
            .addInterceptors(jwtHandshakeInterceptor)
            .setAllowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*");
    }
}
