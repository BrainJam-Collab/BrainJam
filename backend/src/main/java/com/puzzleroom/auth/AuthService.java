package com.puzzleroom.auth;

import com.puzzleroom.auth.dto.AuthResponse;
import com.puzzleroom.auth.dto.LoginRequest;
import com.puzzleroom.auth.dto.RegisterRequest;
import com.puzzleroom.user.User;
import com.puzzleroom.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;

    public AuthService(UserRepository users, PasswordEncoder encoder, JwtUtil jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public AuthResponse register(RegisterRequest req) {
        String email = req.email.toLowerCase().trim();
        String username = req.username.toLowerCase().trim();

        if (users.existsByEmail(email)) {
            throw new IllegalArgumentException("email already in use");
        }
        if (users.existsByUsername(username)) {
            throw new IllegalArgumentException("username already in use");
        }

        String hash = encoder.encode(req.password);
        User u = users.save(new User(email, username, hash));

        String token = jwt.createToken(u.getId().toString(), u.getEmail());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest req) {
        String identifier = firstNonBlank(req.identifier, req.username, req.email);
        if (identifier == null) {
            throw new IllegalArgumentException("identifier required");
        }
        identifier = identifier.toLowerCase().trim();
        User u = identifier.contains("@")
                ? users.findByEmail(identifier).orElse(null)
                : users.findByUsername(identifier).orElse(null);
        if (u == null) {
            throw new IllegalArgumentException("invalid credentials");
        }

        if (!encoder.matches(req.password, u.getPasswordHash())) {
            throw new IllegalArgumentException("invalid credentials");
        }

        String token = jwt.createToken(u.getId().toString(), u.getEmail());
        return new AuthResponse(token);
    }

    private String firstNonBlank(String... values) {
        if (values == null) return null;
        for (String v : values) {
            if (v != null && !v.trim().isEmpty()) return v;
        }
        return null;
    }
}
