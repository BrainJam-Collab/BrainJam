package com.puzzleroom.realtime;

import com.puzzleroom.auth.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwt;

    public JwtHandshakeInterceptor(JwtUtil jwt) {
        this.jwt = jwt;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        String token = extractToken(request.getURI());
        if (token == null || token.isBlank()) {
            return false;
        }

        try {
            Claims claims = jwt.parseClaims(token);
            attributes.put("userId", claims.getSubject());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) {}

    private String extractToken(URI uri) {
        String query = uri.getQuery();
        if (query == null) return null;
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && kv[0].equals("token")) {
                return kv[1];
            }
        }
        return null;
    }
}
