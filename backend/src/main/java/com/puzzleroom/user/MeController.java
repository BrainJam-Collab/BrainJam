package com.puzzleroom.user;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
public class MeController {

    private final UserRepository users;

    public MeController(UserRepository users) {
        this.users = users;
    }

    @GetMapping("/me")
    public Map<String, Object> me(Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());

        User u = users.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        return Map.of(
                "id", u.getId().toString(),
                "username", u.getUsername()
        );
    }
}
