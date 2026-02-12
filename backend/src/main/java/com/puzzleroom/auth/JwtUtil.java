package com.puzzleroom.auth;

import com.puzzleroom.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil {

    private final JwtConfig cfg;

    public JwtUtil(JwtConfig cfg) {
        this.cfg = cfg;
    }

    public String createToken(String userId, String email) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(cfg.getExpiresSeconds());

        return Jwts.builder()
                .subject(userId)
                .claim("email", email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(Keys.hmacShaKeyFor(cfg.getSecret().getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public Claims parseClaims(String token) {
        return (Claims) Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(cfg.getSecret().getBytes(StandardCharsets.UTF_8)))
                .build()
                .parse(token)
                .getPayload();
    }
}
